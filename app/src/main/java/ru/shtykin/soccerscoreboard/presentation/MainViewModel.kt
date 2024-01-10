package ru.shtykin.soccerscoreboard.presentation

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.shtykin.bluetooth.domain.entity.BtDevice
import ru.shtykin.bluetooth.domain.usecase.BoundBluetoothDeviceUseCase
import ru.shtykin.bluetooth.domain.usecase.CheckBluetoothStateUseCase
import ru.shtykin.bluetooth.domain.usecase.ConnectBtDeviceUseCase
import ru.shtykin.bluetooth.domain.usecase.DisconnectBtDeviceUseCase
import ru.shtykin.bluetooth.domain.usecase.GetBluetoothDeviceFlowUseCase
import ru.shtykin.bluetooth.domain.usecase.GetBoundedBluetoothDevicesUseCase
import ru.shtykin.bluetooth.domain.usecase.GetIsBluetoothDiscoveringFlowUseCase
import ru.shtykin.bluetooth.domain.usecase.SendMessageUseCase
import ru.shtykin.bluetooth.domain.usecase.StartDiscoveryUseCase
import ru.shtykin.bluetooth.extension.filterBoundedDevice
import ru.shtykin.soccerscoreboard.domain.entity.Game
import ru.shtykin.soccerscoreboard.domain.entity.Team
import ru.shtykin.soccerscoreboard.domain.usecase.GetGameUseCase
import ru.shtykin.soccerscoreboard.domain.usecase.SaveGameUseCase
import ru.shtykin.soccerscoreboard.presentation.state.ScreenState
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val checkBluetoothStateUseCase: CheckBluetoothStateUseCase,
    private val getBoundedBluetoothDevicesUseCase: GetBoundedBluetoothDevicesUseCase,
    private val startDiscoveryUseCase: StartDiscoveryUseCase,
    private val getBluetoothDeviceFlowUseCase: GetBluetoothDeviceFlowUseCase,
    private val getIsBluetoothDiscoveringFlowUseCase: GetIsBluetoothDiscoveringFlowUseCase,
    private val boundBluetoothDeviceUseCase: BoundBluetoothDeviceUseCase,
    private val connectBtDeviceUseCase: ConnectBtDeviceUseCase,
    private val disconnectBtDeviceUseCase: DisconnectBtDeviceUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val saveGameUseCase: SaveGameUseCase,
    private val getGameUseCase: GetGameUseCase
) : ViewModel() {

    private val _uiState =
        mutableStateOf<ScreenState>(
            ScreenState.SettingsScreen(
                game = getGame(),
                boundedDevices = getBondedDevices(),
                onlineDevices = emptyList(),
                isDiscovering = false
            )
        )

    val uiState: State<ScreenState>
        get() = _uiState

    private val listDevices = mutableListOf<BtDevice>()

    init {
        Log.e("DEBUG1", "bt -> ${checkBluetoothStateUseCase.execute()}")

        viewModelScope.launch(Dispatchers.IO) {
            getBtDevicesFlow().collect {
                listDevices.add(it)
                withContext(Dispatchers.Main) {
                    val currentState = _uiState.value
                    if (currentState is ScreenState.SettingsScreen) {
                        _uiState.value = currentState.copy(
                            onlineDevices = listDevices.toList().filterBoundedDevice(getBondedDevices())
                        )
                    }
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            getIsBtDiscoveringFlow().collect {
                withContext(Dispatchers.Main) {
                    val currentState = _uiState.value
                    if (currentState is ScreenState.SettingsScreen) {
                        val currentOnlineDevices = currentState.onlineDevices
                        _uiState.value = currentState.copy(
                            onlineDevices = currentOnlineDevices.filterBoundedDevice(getBondedDevices()),
                            isDiscovering = false,
                            boundedDevices = getBondedDevices()
                        )
                    }

                }
            }
        }
    }

    fun startDiscovery() {
        viewModelScope.launch(Dispatchers.IO) {
            if (startDiscoveryUseCase.execute()) {
                listDevices.clear()
                withContext(Dispatchers.Main) {
                    _uiState.value = ScreenState.SettingsScreen(
                        game = getGame(),
                        boundedDevices = getBondedDevices(),
                        onlineDevices = listDevices.toList(),
                        isDiscovering = true
                    )
                }
            }
        }

    }

    fun boundDevice(btDevice: BtDevice) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                boundBluetoothDeviceUseCase.execute(btDevice)
            } catch (e: Exception) {
                Log.e("DEBUG1", "ex -> ${e.message}")
            }
        }

    }

    fun connectDevice(btDevice: BtDevice) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                connectBtDeviceUseCase.execute(btDevice)
            } catch (e: Exception) {
                Log.e("DEBUG1", "ex -> ${e.message}")
            }
        }
    }

    fun disconnectDevice() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                disconnectBtDeviceUseCase.execute()
            } catch (e: Exception) {
                Log.e("DEBUG1", "ex -> ${e.message}")
            }
        }
    }

    fun colorPickedTeam(team: Team, color: Color) {
        viewModelScope.launch(Dispatchers.IO) {
            val game = getGame()
            if (team == game.team1) {
                game.team1.color = color
            } else if (team == game.team2) {
                game.team2.color = color
            }
            saveGame(game)
            withContext(Dispatchers.Main) {
                val currentState = _uiState.value
                if (currentState is ScreenState.SettingsScreen) {
                    _uiState.value = currentState.copy(
                        game = getGame()
                    )
                }
            }
        }

    }

    fun sendMessage(text: String) = sendMessageUseCase.execute(text)
    private fun getGame() = getGameUseCase.execute()
    private fun saveGame(game: Game) = saveGameUseCase.execute(game)
    private fun getBondedDevices() = getBoundedBluetoothDevicesUseCase.execute()
    private fun getBtDevicesFlow() = getBluetoothDeviceFlowUseCase.execute()
    private fun getIsBtDiscoveringFlow() = getIsBluetoothDiscoveringFlowUseCase.execute()

}
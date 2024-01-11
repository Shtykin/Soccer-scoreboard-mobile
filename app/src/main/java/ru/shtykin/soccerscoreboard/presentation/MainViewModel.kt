package ru.shtykin.soccerscoreboard.presentation

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.shtykin.bluetooth.domain.entity.BtDevice
import ru.shtykin.bluetooth.domain.entity.Game
import ru.shtykin.bluetooth.domain.entity.Team
import ru.shtykin.bluetooth.domain.usecase.BoundBluetoothDeviceUseCase
import ru.shtykin.bluetooth.domain.usecase.GetBluetoothStateFlowUseCase
import ru.shtykin.bluetooth.domain.usecase.ConnectBtDeviceUseCase
import ru.shtykin.bluetooth.domain.usecase.DisconnectBtDeviceUseCase
import ru.shtykin.bluetooth.domain.usecase.GetBluetoothDeviceFlowUseCase
import ru.shtykin.bluetooth.domain.usecase.GetBluetoothStateUseCase
import ru.shtykin.bluetooth.domain.usecase.GetBoundedBluetoothDevicesUseCase
import ru.shtykin.bluetooth.domain.usecase.GetGameFlowUseCase
import ru.shtykin.bluetooth.domain.usecase.GetGameUseCase
import ru.shtykin.bluetooth.domain.usecase.GetIsBluetoothDiscoveringFlowUseCase
import ru.shtykin.bluetooth.domain.usecase.SaveGameUseCase
import ru.shtykin.bluetooth.domain.usecase.SendMessageUseCase
import ru.shtykin.bluetooth.domain.usecase.StartDiscoveryUseCase
import ru.shtykin.bluetooth.extension.filterBoundedDevice
import ru.shtykin.soccerscoreboard.presentation.state.ScreenState
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val getBluetoothStateFlowUseCase: GetBluetoothStateFlowUseCase,
    private val getBluetoothStateUseCase: GetBluetoothStateUseCase,
    private val getBoundedBluetoothDevicesUseCase: GetBoundedBluetoothDevicesUseCase,
    private val startDiscoveryUseCase: StartDiscoveryUseCase,
    private val getBluetoothDeviceFlowUseCase: GetBluetoothDeviceFlowUseCase,
    private val getIsBluetoothDiscoveringFlowUseCase: GetIsBluetoothDiscoveringFlowUseCase,
    private val boundBluetoothDeviceUseCase: BoundBluetoothDeviceUseCase,
    private val connectBtDeviceUseCase: ConnectBtDeviceUseCase,
    private val disconnectBtDeviceUseCase: DisconnectBtDeviceUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val saveGameUseCase: SaveGameUseCase,
    private val getGameUseCase: GetGameUseCase,
    private val getGameFlowUseCase: GetGameFlowUseCase
) : ViewModel() {

    private val _uiState =
        mutableStateOf<ScreenState>(
            ScreenState.SettingsScreen(
                bluetoothState = getBluetoothState(),
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
//        Log.e("DEBUG1", "bt -> ${getBluetoothStateFlowUseCase.execute()}")

        viewModelScope.launch(Dispatchers.IO) {
            getGameFlow().collect {game ->
                withContext(Dispatchers.Main) {
                    val currentState = _uiState.value
                    if (currentState is ScreenState.GameScreen) {
                        _uiState.value = currentState.copy(
                            game = game
                        )
                    }
                }
            }
        }

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
        viewModelScope.launch(Dispatchers.IO) {
            getBluetoothStateFlow().collect{state ->
                withContext(Dispatchers.Main) {
                    val currentState = _uiState.value
                    if (currentState is ScreenState.SettingsScreen) {
                        _uiState.value = currentState.copy(
                            bluetoothState = state
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
                        bluetoothState = getBluetoothState(),
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

    fun changeTeamColor(team: Team, color: Color) {
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

    fun changeTeamName(team: Team, name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val game = getGame()
            if (team == game.team1) {
                game.team1.name = name
            } else if (team == game.team2) {
                game.team2.name = name
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

    fun changeHalfTime(time: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val game = getGame()
            saveGame(game.copy(halfTime = time))
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


    fun gameScreenOpened() {
        _uiState.value = ScreenState.GameScreen(
            game = getGame()
        )
        viewModelScope.launch(Dispatchers.IO) {
            while (_uiState.value is ScreenState.GameScreen) {
                sendMessage("Hi!")
                delay(100)
            }
        }
    }

    fun settingScreenOpened() {
        _uiState.value = ScreenState.SettingsScreen(
            bluetoothState = getBluetoothState(),
            game = getGame(),
            boundedDevices = getBondedDevices(),
            onlineDevices = listDevices.toList(),
            isDiscovering = false
        )
    }

    fun sendMessage(text: String) = sendMessageUseCase.execute(text)
    private fun getGame() = getGameUseCase.execute()
    private fun getGameFlow() = getGameFlowUseCase.execute()
    private fun saveGame(game: Game) = saveGameUseCase.execute(game)
    private fun getBondedDevices() = getBoundedBluetoothDevicesUseCase.execute()
    private fun getBtDevicesFlow() = getBluetoothDeviceFlowUseCase.execute()
    private fun getIsBtDiscoveringFlow() = getIsBluetoothDiscoveringFlowUseCase.execute()
    private fun getBluetoothStateFlow() = getBluetoothStateFlowUseCase.execute()
    private fun getBluetoothState() = getBluetoothStateUseCase.execute()

}
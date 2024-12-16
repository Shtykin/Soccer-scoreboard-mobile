package ru.shtykin.soccerscoreboard.presentation

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.shtykin.bluetooth.domain.entity.BluetoothState
import ru.shtykin.bluetooth.domain.entity.BtDevice
import ru.shtykin.bluetooth.domain.entity.DevParam
import ru.shtykin.bluetooth.domain.entity.Game
import ru.shtykin.bluetooth.domain.entity.request.RequestMessageControl
import ru.shtykin.bluetooth.domain.entity.request.RequestMessageData
import ru.shtykin.bluetooth.domain.entity.request.RequestMessageSettings
import ru.shtykin.bluetooth.domain.entity.request.RequestControl
import ru.shtykin.bluetooth.domain.entity.request.RequestData
import ru.shtykin.bluetooth.domain.entity.response.ResponseSettings
import ru.shtykin.bluetooth.domain.entity.Team
import ru.shtykin.bluetooth.domain.entity.request.RequestSettings
import ru.shtykin.bluetooth.domain.entity.response.ResponseControl
import ru.shtykin.bluetooth.domain.entity.response.ResponseData
import ru.shtykin.bluetooth.domain.usecase.BoundBluetoothDeviceUseCase
import ru.shtykin.bluetooth.domain.usecase.ConnectBtDeviceUseCase
import ru.shtykin.bluetooth.domain.usecase.DisconnectBtDeviceUseCase
import ru.shtykin.bluetooth.domain.usecase.GetBluetoothDeviceFlowUseCase
import ru.shtykin.bluetooth.domain.usecase.GetBluetoothStateFlowUseCase
import ru.shtykin.bluetooth.domain.usecase.GetBluetoothStateUseCase
import ru.shtykin.bluetooth.domain.usecase.GetBoundedBluetoothDevicesUseCase
import ru.shtykin.bluetooth.domain.usecase.GetControlFlowUseCase
import ru.shtykin.bluetooth.domain.usecase.GetDataFlowUseCase
import ru.shtykin.bluetooth.domain.usecase.GetGameFlowUseCase
import ru.shtykin.bluetooth.domain.usecase.GetGameUseCase
import ru.shtykin.bluetooth.domain.usecase.GetIsBluetoothDiscoveringFlowUseCase
import ru.shtykin.bluetooth.domain.usecase.GetRawMsgFlowUseCase
import ru.shtykin.bluetooth.domain.usecase.GetSettingsFlowUseCase
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
    private val getGameFlowUseCase: GetGameFlowUseCase,
    private val getRawMsgUseCase: GetRawMsgFlowUseCase,
    private val getDataFlowUseCase: GetDataFlowUseCase,
    private val getControlFlowUseCase: GetControlFlowUseCase,
    private val getSettingsFlowUseCase: GetSettingsFlowUseCase,
) : ViewModel() {

    private val _msg = MutableStateFlow("")
    val msg: StateFlow<String> = _msg

    private val _isWriteLogs = MutableStateFlow(false)
    val isWriteLogs: StateFlow<Boolean> = _isWriteLogs

    private val _responseControl: MutableStateFlow<ResponseControl> = MutableStateFlow(
        ResponseControl(
            enable = false,
            reset = false,
            reverse = false,
        )
    )
    val responseControl: StateFlow<ResponseControl> = _responseControl

    private val _responseSettings: MutableStateFlow<ResponseSettings> = MutableStateFlow(
        ResponseSettings(
            scoreShow = false,
            scoreTime = 0,
            scoreDuration = 0,
            colorCount = 0,
            colorScore = 0,
            colorFault = 0,
            brightnessCount = 0,
            brightnessFault = 0,
            brightnessScore = 0,
            brightnessTeam = 0,
        )
    )
    val responseSettings: StateFlow<ResponseSettings> = _responseSettings

    private val _responseData: MutableStateFlow<ResponseData> = MutableStateFlow(
        ResponseData(
            count = 0,
            fullTime = 0,
            nameT1 = "Команда 1",
            nameT2 = "Команда 2",
            colorT1 = 0,
            colorT2 = 0,
            scoreT1 = 0,
            scoreT2 = 0,
            faultT1 = 0,
            faultT2 = 0,
        )
    )
    val responseData: StateFlow<ResponseData> = _responseData

    private val _btState = MutableStateFlow(BluetoothState.DISABLED)

    private val _uiState =
        mutableStateOf<ScreenState>(
            ScreenState.GameScreen(
                game = getGame()
            )
        )

    val uiState: State<ScreenState>
        get() = _uiState

    private val listDevices = mutableListOf<BtDevice>()

    init {

        viewModelScope.launch(Dispatchers.IO) {
            getControlFlow().collect { control ->
                withContext(Dispatchers.Main) {
                    _responseControl.value = control
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            getSettingsFlow().collect { settings ->
                withContext(Dispatchers.Main) {
                    _responseSettings.value = settings
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            getDataFlow().collect { data ->
                withContext(Dispatchers.Main) {
                    _responseData.value = data
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            getRawMsgFlow().collect { msg ->
                if (_isWriteLogs.value) {
                    withContext(Dispatchers.Main) {
                        _msg.value += "\n$msg"
                    }
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            getGameFlow().collect { game ->
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
                            onlineDevices = listDevices.toList()
                                .filterBoundedDevice(getBondedDevices())
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
                            onlineDevices = currentOnlineDevices.filterBoundedDevice(
                                getBondedDevices()
                            ),
                            isDiscovering = false,
                            boundedDevices = getBondedDevices()
                        )
                    }
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            getBluetoothStateFlow().collect { state ->
                _btState.value = state

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
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                if (_btState.value == BluetoothState.CONNECTED) sendMessage("data")
                delay(333)
                if (_btState.value == BluetoothState.CONNECTED) sendMessage("settings")
                delay(333)
                if (_btState.value == BluetoothState.CONNECTED) sendMessage("control")
                delay(333)
            }
        }

    }

    fun setIsWriteLogs(enabled: Boolean) {
        _isWriteLogs.value = enabled
    }

    fun clearLogs() {
        _msg.value = ""
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
            setData(RequestMessageData(RequestData(fullTime = time)))
//            val game = getGame()
//            saveGame(game.copy(halfTime = time))
//            withContext(Dispatchers.Main) {
//                val currentState = _uiState.value
//                if (currentState is ScreenState.SettingsScreen) {
//                    _uiState.value = currentState.copy(
//                        game = getGame()
//                    )
//                }
//            }
        }
    }

    fun changeParamValue(param: DevParam, value: String) {

    }


    fun gameScreenOpened() {
        _uiState.value = ScreenState.GameScreen(
            game = getGame()
        )
//        viewModelScope.launch(Dispatchers.IO) {
//            while (_uiState.value is ScreenState.GameScreen) {
//                sendMessage("data")
//                delay(500)
//                sendMessage("settings")
//                delay(500)
//                sendMessage("control")
//                delay(500)
//            }
//        }
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

    fun developerScreenOpened() {
        _uiState.value = ScreenState.DeveloperScreen(
            devParams = listOf(
                DevParam("Время 1", "12:34"),
                DevParam("Время 2", "32:34"),
                DevParam("Время 3", "22:34"),
                DevParam("Время 4", "55:34"),
                DevParam("Время 4", "55:34"),
                DevParam("Время 4", "55:34"),
                DevParam("Время 4", "55:34"),
                DevParam("Время 4", "55:34"),
                DevParam("Время 4", "55:34"),
                DevParam("Время 4", "55:34"),
                DevParam("Время 4", "55:34"),
                DevParam("Время 4", "55:34"),
                DevParam("Время 4", "55:34"),
                DevParam("Время 4", "55:34"),
                DevParam("Время 4", "55:34"),
                DevParam("Время 4", "55:34"),
                DevParam("Время 4", "55:34"),
            )
        )
    }

    fun start(enable: Boolean) {
        setControl(RequestMessageControl(RequestControl(enable = enable)))
    }

    fun addScoreT1(){
        setData(RequestMessageData(RequestData(scoreT1 = _responseData.value.scoreT1 + 1)))
    }
    fun addScoreT2(){
        setData(RequestMessageData(RequestData(scoreT2 = _responseData.value.scoreT2 + 1)))
    }
    fun minusScoreT1(){
        if (_responseData.value.scoreT1 <= 0) return
        setData(RequestMessageData(RequestData(scoreT1 = _responseData.value.scoreT1 - 1)))
    }
    fun minusScoreT2(){
        if (_responseData.value.scoreT2 <= 0) return
        setData(RequestMessageData(RequestData(scoreT2 = _responseData.value.scoreT2 - 1)))
    }

    fun plusFaultT1(){
        if (_responseData.value.faultT1 >= 5) return
        setData(RequestMessageData(RequestData(faultT1 = _responseData.value.faultT1 + 1)))
    }

    fun plusFaultT2(){
        if (_responseData.value.faultT2 >= 5) return
        setData(RequestMessageData(RequestData(faultT2 = _responseData.value.faultT2 + 1)))
    }

    fun minusFaultT1(){
        if (_responseData.value.faultT1 <= 0) return
        setData(RequestMessageData(RequestData(faultT1 = _responseData.value.faultT1 - 1)))
    }

    fun minusFaultT2(){
        if (_responseData.value.faultT2 <= 0) return
        setData(RequestMessageData(RequestData(faultT2 = _responseData.value.faultT2 - 1)))
    }

    fun changeTeamAreas() {
        setControl(RequestMessageControl(RequestControl(reverse = !_responseControl.value.reverse)))
    }

    fun setNameT1(name: String) {
        setData(RequestMessageData(RequestData(nameT1 = name)))
    }
    fun setNameT2(name: String) {
        setData(RequestMessageData(RequestData(nameT2 = name)))
    }
    fun setColorT1(color: Color) {
        setData(RequestMessageData(RequestData(colorT1 = color.toArgb())))
    }
    fun setColorT2(color: Color) {
        setData(RequestMessageData(RequestData(colorT2 = color.toArgb())))
    }

    fun setScoreShow(scoreShow: Int) {
        val boolean = scoreShow != 0
        setSettings(RequestMessageSettings(RequestSettings(scoreShow = boolean)))
    }

    fun setScoreTime(scoreTime: Int) {
        setSettings(RequestMessageSettings(RequestSettings(scoreTime = scoreTime)))
    }

    fun setScoreDuration(scoreDuration: Int) {
        setSettings(RequestMessageSettings(RequestSettings(scoreDuration = scoreDuration)))
    }
    fun setColorCount(colorCount: Int) {
        setSettings(RequestMessageSettings(RequestSettings(colorCount = colorCount)))
    }
    fun setColorScore(colorScore: Int) {
        setSettings(RequestMessageSettings(RequestSettings(colorScore = colorScore)))
    }
    fun setColorFault(colorFault: Int) {
        setSettings(RequestMessageSettings(RequestSettings(colorFault = colorFault)))
    }
    fun setBrightnessCount(brightnessCount: Int) {
        setSettings(RequestMessageSettings(RequestSettings(brightnessCount = brightnessCount)))
    }
    fun setBrightnessFault(brightnessFault: Int) {
        setSettings(RequestMessageSettings(RequestSettings(brightnessFault = brightnessFault)))
    }
    fun setBrightnessScore(brightnessScore: Int) {
        setSettings(RequestMessageSettings(RequestSettings(brightnessScore = brightnessScore)))
    }

    private fun sendMessage(text: String) = sendMessageUseCase.execute(text)

    private fun setData(data: RequestMessageData) = sendMessageUseCase.execute(Json.encodeToString(data))
    private fun setSettings(settings: RequestMessageSettings) =
        sendMessageUseCase.execute(Json.encodeToString(settings))

    private fun setControl(control: RequestMessageControl) =
        sendMessageUseCase.execute(Json.encodeToString(control))

    private fun getGame() = getGameUseCase.execute()
    private fun getGameFlow() = getGameFlowUseCase.execute()
    private fun getRawMsgFlow() = getRawMsgUseCase.execute()
    private fun getControlFlow() = getControlFlowUseCase.execute()
    private fun getDataFlow() = getDataFlowUseCase.execute()
    private fun getSettingsFlow() = getSettingsFlowUseCase.execute()
    private fun saveGame(game: Game) = saveGameUseCase.execute(game)
    private fun getBondedDevices() = getBoundedBluetoothDevicesUseCase.execute()
    private fun getBtDevicesFlow() = getBluetoothDeviceFlowUseCase.execute()
    private fun getIsBtDiscoveringFlow() = getIsBluetoothDiscoveringFlowUseCase.execute()
    private fun getBluetoothStateFlow() = getBluetoothStateFlowUseCase.execute()
    private fun getBluetoothState() = getBluetoothStateUseCase.execute()

}
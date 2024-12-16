package ru.shtykin.bluetooth.data.repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import ru.shtykin.bluetooth.data.mapper.Mapper
import ru.shtykin.bluetooth.domain.Repository
import ru.shtykin.bluetooth.domain.entity.BluetoothState
import ru.shtykin.bluetooth.domain.entity.BtDevice
import ru.shtykin.bluetooth.domain.entity.request.RequestControl
import ru.shtykin.bluetooth.domain.entity.Game
import ru.shtykin.bluetooth.domain.entity.request.RequestMessageControl
import ru.shtykin.bluetooth.domain.entity.request.RequestMessageData
import ru.shtykin.bluetooth.domain.entity.request.RequestMessageSettings
import ru.shtykin.bluetooth.domain.entity.response.ResponseSettings
import ru.shtykin.bluetooth.domain.entity.Team
import ru.shtykin.bluetooth.domain.entity.response.ResponseControl
import ru.shtykin.bluetooth.domain.entity.response.ResponseData
import ru.shtykin.bluetooth.domain.entity.response.ResponseMessageControl
import ru.shtykin.bluetooth.domain.entity.response.ResponseMessageData
import ru.shtykin.bluetooth.domain.entity.response.ResponseMessageSettings
import java.io.IOException
import java.util.UUID

@OptIn(DelicateCoroutinesApi::class)
class RepositoryImpl(
    private val mapper: Mapper,
    private val appContext: Context,
    private val bluetoothAdapter: BluetoothAdapter
) : Repository {

    private val uuid = "00001101-0000-1000-8000-00805F9B34FB"
    private var bluetoothDeviceFlow = MutableSharedFlow<BluetoothDevice>()
    private var isBluetoothDiscoveringFlow = MutableSharedFlow<Unit?>()
    private var bluetoothStateFlow = MutableSharedFlow<BluetoothState>()
    private var gameFlow = MutableSharedFlow<Game>()
    private var rawMsgFlow = MutableSharedFlow<String>()
    private var currentBtState = getBluetoothState()
    private var responseControlFlow = MutableSharedFlow<ResponseControl>()
    private var responseSettingsFlow = MutableSharedFlow<ResponseSettings>()
    private var responseDataFlow = MutableSharedFlow<ResponseData>()
    var mSocket: BluetoothSocket? = null


    val scope = GlobalScope
//    init {
//        scope.launch {
//            while (true) {
//                rawMsgFlow.emit("123")
//                delay(1000)
//            }
//
//        }
//    }


    private var game = Game(
        team1 = Team( "Команда 1", Color.Red),
        team2 = Team( "Команда 2", Color.Blue),
        halfTime = 30 * 60,
        currentTime = 0
    )

    override fun saveGame(game: Game) {
        this.game = game
    }

    override fun getGame() = game

    @SuppressLint("MissingPermission")
    override fun getBoundedBluetoothDevices(): List<BtDevice> {
        return try {
            bluetoothAdapter.bondedDevices.map { mapper.mapBluetoothDeviceToBTDevice(it) }
        } catch (e: Exception) {
            emptyList()
        }

    }

    @SuppressLint("MissingPermission")
    override fun startDiscovery(): Boolean {
        if (bluetoothAdapter.isDiscovering) return false
        bluetoothAdapter.startDiscovery()
        return true
    }

    @SuppressLint("MissingPermission")
    override suspend fun boundBtDevice(mac: String): Boolean {
        if (bluetoothAdapter.isDiscovering) bluetoothAdapter.cancelDiscovery()
        while (bluetoothAdapter.isDiscovering) {
            delay(100)
        }
        val device = bluetoothAdapter.getRemoteDevice(mac)
        return device.createBond()
    }

    override suspend fun emitDeviceToFlow(bluetoothDevice: BluetoothDevice) {
        bluetoothDeviceFlow.emit(bluetoothDevice)
    }

    override suspend fun stopDeviceFlow() {
        isBluetoothDiscoveringFlow.emit(null)
    }

    override fun getBluetoothDeviceFlow(): Flow<BtDevice> = bluetoothDeviceFlow.map { mapper.mapBluetoothDeviceToBTDevice(it) }

    override fun getIsBluetoothDiscoveringFlow(): Flow<Unit?> = isBluetoothDiscoveringFlow

    override suspend fun btConnect(btDevice: BtDevice): Boolean {
        return try {
            Log.e("DEBUG1", "Connecting...")
            val device = bluetoothAdapter.getRemoteDevice(btDevice.mac)
            mSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(uuid))
            mSocket?.connect()
            Log.e("DEBUG1", "Connect!!!")
            inputMessage()
            true
        } catch (ioe: IOException) {
            false
        } catch (se: SecurityException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun btDisconnect() {
        try {
            mSocket?.close()
            checkAndEmitBtState()
        } catch (ioe: IOException) {

        } catch (se: SecurityException) {

        } catch (e: Exception) {

        }
    }

    override fun getBluetoothStateFlow(): Flow<BluetoothState> = bluetoothStateFlow

    private suspend fun inputMessage() {
        val buffer = ByteArray(256)
        while(true) {
            checkAndEmitBtState()
            try {
                val length = mSocket?.inputStream?.read(buffer)
                val msg = String(buffer, 0, length ?: 0).replace("\n", "").replace("null", "")
                if( msg.isNotEmpty()) {
                    rawMsgFlow.emit("<- $msg")
                    try {
                        if (msg.contains("control")) {
                            val control = Json.decodeFromString<ResponseMessageControl>(msg)
                            rawMsgFlow.emit("control -> $control")
                            responseControlFlow.emit(control.responseControl)
                        }
                    } catch (e: Exception) {
                        Log.e("DEBUG1", "Exception -> ${e.message}")
                        rawMsgFlow.emit("control exception -> ${e.message}")
                    }
                    try {
                        if (msg.contains("settings")) {
                            val settings = Json.decodeFromString<ResponseMessageSettings>(msg)
                            rawMsgFlow.emit("settings -> $settings")
                            responseSettingsFlow.emit(settings.responseSettings)
                        }
                    } catch (e: Exception) {
                        Log.e("DEBUG1", "Exception -> ${e.message}")
                        rawMsgFlow.emit("settings exception -> ${e.message}")
                    }
                    try {
                        if (msg.contains("data")) {
                            val data = Json.decodeFromString<ResponseMessageData>(msg)
                            rawMsgFlow.emit("data -> $data")
                            responseDataFlow.emit(data.responseData)
                        }
                    } catch (e: Exception) {
                        Log.e("DEBUG1", "Exception -> ${e.message}")
                        rawMsgFlow.emit("data exception -> ${e.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("DEBUG1", "inputMessage Exception -> ${e.message}")
                break
            }
        }
    }



    override fun sendMsg(msg: String) {
        try {
            if( msg.isNotEmpty()) Log.e("DEBUG1", "output msg -> $msg")
            mSocket?.outputStream?.write(("$msg\n\n").toByteArray())
            scope.launch { rawMsgFlow.emit("-> $msg\n") }
        } catch (e: Exception) {
            Log.e("DEBUG1", "sendMsg -> ${e.message}")
        }
    }


    override fun getBluetoothState(): BluetoothState {
        if (mSocket?.isConnected == true) return BluetoothState.CONNECTED
        return when (bluetoothAdapter.isEnabled) {
            true -> BluetoothState.ENABLED
            else -> BluetoothState.DISABLED
        }
    }

    override fun getGameFlow() = gameFlow

    override fun getRawMsgFlow(): Flow<String> = rawMsgFlow

    override fun getControlFlow(): Flow<ResponseControl> = responseControlFlow

    override fun getSettingsFlow(): Flow<ResponseSettings> = responseSettingsFlow

    override fun getDataFlow(): Flow<ResponseData> = responseDataFlow

    override suspend fun checkAndEmitBtState() {
        val btState = getBluetoothState()
        if (btState != currentBtState) {
            currentBtState = btState
            bluetoothStateFlow.emit(currentBtState)
        }
    }
}
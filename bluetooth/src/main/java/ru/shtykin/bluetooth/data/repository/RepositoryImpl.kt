package ru.shtykin.bluetooth.data.repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import ru.shtykin.bluetooth.domain.Repository
import ru.shtykin.bluetooth.data.mapper.Mapper
import ru.shtykin.bluetooth.domain.entity.BluetoothState
import ru.shtykin.bluetooth.domain.entity.BtDevice
import ru.shtykin.bluetooth.domain.entity.Game
import ru.shtykin.bluetooth.domain.entity.Team
import java.io.IOException
import java.util.UUID

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
    private var currentBtState = getBluetoothState()
    var mSocket: BluetoothSocket? = null


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
                val msg = String(buffer, 0, length ?: 0)
                if( msg.isNotEmpty()) {
                    try {
                        val time = msg.filter { it.isDigit() }.toInt()
                        game = game.copy(currentTime = time)
                        gameFlow.emit(game)
                        Log.e("DEBUG1", "input msg -> $msg")
                    } catch (e: Exception) {
                        Log.e("DEBUG1", "Exception -> ${e.message}")
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
            mSocket?.outputStream?.write(("$msg\n").toByteArray())
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

    override suspend fun checkAndEmitBtState() {
        val btState = getBluetoothState()
        if (btState != currentBtState) {
            currentBtState = btState
            bluetoothStateFlow.emit(currentBtState)
        }
    }
}
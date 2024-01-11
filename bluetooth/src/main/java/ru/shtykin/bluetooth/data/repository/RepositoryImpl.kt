package ru.shtykin.bluetooth.data.repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import ru.shtykin.bluetooth.Repository
import ru.shtykin.bluetooth.data.mapper.Mapper
import ru.shtykin.bluetooth.domain.entity.BluetoothState
import ru.shtykin.bluetooth.domain.entity.BtDevice
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
    var mSocket: BluetoothSocket? = null


    override fun getBluetoothState(): BluetoothState {
        return when (bluetoothAdapter.isEnabled) {
            true -> BluetoothState.ENABLED
            else -> BluetoothState.DISABLED
        }
    }

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

    override fun btConnect(btDevice: BtDevice): Boolean {
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

    override fun btDisconnect() {
        try {
            mSocket?.close()
        } catch (ioe: IOException) {

        } catch (se: SecurityException) {

        } catch (e: Exception) {

        }
    }

    private fun inputMessage() {
        val buffer = ByteArray(256)
        while(true) {
            try {
                val length = mSocket?.inputStream?.read(buffer)
                val msg = String(buffer, 0, length ?: 0)
                if( msg.isNotEmpty()) Log.e("DEBUG1", "msg -> $msg")
            } catch (e: Exception) {
                Log.e("DEBUG1", "inputMessage -> ${e.message}")
                break
            }
        }
    }

    override fun sendMsg(msg: String) {
        try {
            mSocket?.outputStream?.write(msg.toByteArray())
        } catch (e: Exception) {
            Log.e("DEBUG1", "sendMsg -> ${e.message}")
        }
    }
}
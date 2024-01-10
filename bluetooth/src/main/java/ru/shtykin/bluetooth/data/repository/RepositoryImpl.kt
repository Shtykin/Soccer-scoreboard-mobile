package ru.shtykin.bluetooth.data.repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import ru.shtykin.bluetooth.Repository
import ru.shtykin.bluetooth.data.mapper.Mapper
import ru.shtykin.bluetooth.domain.entity.BluetoothState
import ru.shtykin.bluetooth.domain.entity.BtDevice

class RepositoryImpl(
    private val mapper: Mapper,
    private val appContext: Context,
    private val bluetoothAdapter: BluetoothAdapter
) : Repository {

    private var bluetoothDeviceFlow = MutableSharedFlow<BluetoothDevice>()
    private var isBluetoothDiscoveringFlow = MutableSharedFlow<Unit?>()


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
}
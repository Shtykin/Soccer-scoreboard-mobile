package ru.shtykin.bluetooth.data.repository

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import ru.shtykin.bluetooth.Repository
import ru.shtykin.bluetooth.data.mapper.Mapper
import ru.shtykin.bluetooth.domain.entity.BTDevice
import ru.shtykin.bluetooth.domain.entity.BluetoothState

class RepositoryImpl(
    private val mapper: Mapper,
    private val bluetoothAdapter: BluetoothAdapter
) : Repository {
    override fun getBluetoothState(): BluetoothState {
        return when (bluetoothAdapter.isEnabled) {
            true -> BluetoothState.ENABLED
            else -> BluetoothState.DISABLED
        }
    }

    @SuppressLint("MissingPermission")
    override fun getBluetoothDevices(): List<BTDevice> {
        return try {
            bluetoothAdapter.bondedDevices.map { mapper.mapBluetoothDeviceToBTDevice(it) }
        } catch (e: Exception) {
            emptyList()
        }

    }

    @SuppressLint("MissingPermission")
    override fun startDiscovery() {
        bluetoothAdapter.startDiscovery()
    }
}
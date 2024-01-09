package ru.shtykin.bluetooth

import ru.shtykin.bluetooth.domain.entity.BTDevice
import ru.shtykin.bluetooth.domain.entity.BluetoothState

interface Repository {
    fun getBluetoothState() : BluetoothState
    fun getBluetoothDevices() : List<BTDevice>
    fun startDiscovery()
}
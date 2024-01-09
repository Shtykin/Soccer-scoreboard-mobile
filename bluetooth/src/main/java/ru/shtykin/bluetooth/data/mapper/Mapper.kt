package ru.shtykin.bluetooth.data.mapper

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import ru.shtykin.bluetooth.domain.entity.BTDevice

class Mapper {
    @SuppressLint("MissingPermission")
    fun mapBluetoothDeviceToBTDevice(bluetoothDevice: BluetoothDevice) =
        BTDevice(
            name = bluetoothDevice.name,
            mac = bluetoothDevice.address
        )

}
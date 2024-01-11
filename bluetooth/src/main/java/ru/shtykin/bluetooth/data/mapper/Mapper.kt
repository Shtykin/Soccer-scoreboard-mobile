package ru.shtykin.bluetooth.data.mapper

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import ru.shtykin.bluetooth.domain.entity.BtDevice

class Mapper {
    @SuppressLint("MissingPermission")
    fun mapBluetoothDeviceToBTDevice(bluetoothDevice: BluetoothDevice) =
        BtDevice(
            name = bluetoothDevice.name,
            mac = bluetoothDevice.address
        )

}
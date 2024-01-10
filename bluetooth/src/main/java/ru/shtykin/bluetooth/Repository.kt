package ru.shtykin.bluetooth

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.Flow
import ru.shtykin.bluetooth.domain.entity.BtDevice
import ru.shtykin.bluetooth.domain.entity.BluetoothState

interface Repository {
    fun getBluetoothState() : BluetoothState
    fun getBoundedBluetoothDevices() : List<BtDevice>
    fun startDiscovery() : Boolean
    suspend fun boundBtDevice(mac: String): Boolean
    suspend fun emitDeviceToFlow(bluetoothDevice: BluetoothDevice)
    suspend fun stopDeviceFlow()
    fun getBluetoothDeviceFlow() : Flow<BtDevice>
    fun getIsBluetoothDiscoveringFlow() : Flow<Unit?>
    fun btConnect(btDevice: BtDevice): Boolean
    fun btDisconnect()
    fun sendMsg(msg: String)


}
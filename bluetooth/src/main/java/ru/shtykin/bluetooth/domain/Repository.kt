package ru.shtykin.bluetooth.domain

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.Flow
import ru.shtykin.bluetooth.domain.entity.BtDevice
import ru.shtykin.bluetooth.domain.entity.BluetoothState
import ru.shtykin.bluetooth.domain.entity.Game

interface Repository {
    fun getBoundedBluetoothDevices() : List<BtDevice>
    fun startDiscovery() : Boolean
    suspend fun boundBtDevice(mac: String): Boolean
    suspend fun emitDeviceToFlow(bluetoothDevice: BluetoothDevice)
    suspend fun stopDeviceFlow()
    fun getBluetoothDeviceFlow() : Flow<BtDevice>
    fun getIsBluetoothDiscoveringFlow() : Flow<Unit?>
    suspend fun btConnect(btDevice: BtDevice): Boolean
    fun btDisconnect()
    fun getBluetoothStateFlow(): Flow<BluetoothState>
    fun getBluetoothState(): BluetoothState
    fun sendMsg(msg: String)

    fun saveGame(game: Game)
    fun getGame(): Game
    fun getGameFlow(): Flow<Game>


}
package ru.shtykin.bluetooth.domain

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.Flow
import ru.shtykin.bluetooth.domain.entity.BluetoothState
import ru.shtykin.bluetooth.domain.entity.BtDevice
import ru.shtykin.bluetooth.domain.entity.request.RequestControl
import ru.shtykin.bluetooth.domain.entity.request.RequestData
import ru.shtykin.bluetooth.domain.entity.Game
import ru.shtykin.bluetooth.domain.entity.response.ResponseControl
import ru.shtykin.bluetooth.domain.entity.response.ResponseData
import ru.shtykin.bluetooth.domain.entity.response.ResponseSettings

interface Repository {
    fun getBoundedBluetoothDevices() : List<BtDevice>
    fun startDiscovery() : Boolean
    suspend fun boundBtDevice(mac: String): Boolean
    suspend fun emitDeviceToFlow(bluetoothDevice: BluetoothDevice)
    suspend fun stopDeviceFlow()
    fun getBluetoothDeviceFlow() : Flow<BtDevice>
    fun getIsBluetoothDiscoveringFlow() : Flow<Unit?>
    suspend fun btConnect(btDevice: BtDevice): Boolean
    suspend fun btDisconnect()
    fun getBluetoothStateFlow(): Flow<BluetoothState>
    fun getBluetoothState(): BluetoothState
    fun sendMsg(msg: String)

    fun saveGame(game: Game)
    fun getGame(): Game
    fun getGameFlow(): Flow<Game>
    fun getRawMsgFlow(): Flow<String>

    fun getControlFlow(): Flow<ResponseControl>
    fun getSettingsFlow(): Flow<ResponseSettings>
    fun getDataFlow(): Flow<ResponseData>
    suspend fun checkAndEmitBtState()


}
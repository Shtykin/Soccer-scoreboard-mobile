package ru.shtykin.bluetooth.domain.usecase

import ru.shtykin.bluetooth.Repository
import ru.shtykin.bluetooth.domain.entity.BtDevice


class BoundBluetoothDeviceUseCase (private val repository: Repository) {
    suspend fun execute(btDevice: BtDevice) =
        repository.boundBtDevice(btDevice.mac)
}
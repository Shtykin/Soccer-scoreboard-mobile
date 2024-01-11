package ru.shtykin.bluetooth.domain.usecase

import ru.shtykin.bluetooth.domain.Repository
import ru.shtykin.bluetooth.domain.entity.BtDevice


class ConnectBtDeviceUseCase (private val repository: Repository) {
    suspend fun execute(btDevice: BtDevice) =
        repository.btConnect(btDevice)
}
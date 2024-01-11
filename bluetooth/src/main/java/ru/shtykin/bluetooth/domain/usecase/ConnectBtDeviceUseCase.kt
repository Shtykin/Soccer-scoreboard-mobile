package ru.shtykin.bluetooth.domain.usecase

import ru.shtykin.bluetooth.Repository
import ru.shtykin.bluetooth.domain.entity.BtDevice


class ConnectBtDeviceUseCase (private val repository: Repository) {
    fun execute(btDevice: BtDevice) =
        repository.btConnect(btDevice)
}
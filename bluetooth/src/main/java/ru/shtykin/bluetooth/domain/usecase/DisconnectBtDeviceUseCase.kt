package ru.shtykin.bluetooth.domain.usecase

import ru.shtykin.bluetooth.Repository
import ru.shtykin.bluetooth.domain.entity.BtDevice


class DisconnectBtDeviceUseCase (private val repository: Repository) {
    fun execute() =
        repository.btDisconnect()
}
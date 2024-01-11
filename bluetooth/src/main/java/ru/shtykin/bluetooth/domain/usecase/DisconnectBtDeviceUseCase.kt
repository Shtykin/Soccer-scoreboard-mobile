package ru.shtykin.bluetooth.domain.usecase

import ru.shtykin.bluetooth.domain.Repository


class DisconnectBtDeviceUseCase (private val repository: Repository) {
    fun execute() =
        repository.btDisconnect()
}
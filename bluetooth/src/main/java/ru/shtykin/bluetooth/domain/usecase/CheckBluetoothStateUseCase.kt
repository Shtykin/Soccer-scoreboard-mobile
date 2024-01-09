package ru.shtykin.bluetooth.domain.usecase

import ru.shtykin.bluetooth.Repository


class CheckBluetoothStateUseCase (private val repository: Repository) {
    fun execute() =
        repository.getBluetoothState()
}
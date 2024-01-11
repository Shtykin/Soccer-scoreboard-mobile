package ru.shtykin.bluetooth.domain.usecase

import ru.shtykin.bluetooth.domain.Repository


class GetBluetoothStateUseCase (private val repository: Repository) {
    fun execute() =
        repository.getBluetoothState()
}
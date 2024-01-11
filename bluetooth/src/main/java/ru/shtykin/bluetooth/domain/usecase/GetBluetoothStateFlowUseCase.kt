package ru.shtykin.bluetooth.domain.usecase

import ru.shtykin.bluetooth.domain.Repository


class GetBluetoothStateFlowUseCase (private val repository: Repository) {
    fun execute() =
        repository.getBluetoothStateFlow()
}
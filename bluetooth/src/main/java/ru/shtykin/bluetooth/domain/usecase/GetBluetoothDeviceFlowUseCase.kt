package ru.shtykin.bluetooth.domain.usecase

import ru.shtykin.bluetooth.Repository


class GetBluetoothDeviceFlowUseCase (private val repository: Repository) {
    fun execute() =
        repository.getBluetoothDeviceFlow()
}
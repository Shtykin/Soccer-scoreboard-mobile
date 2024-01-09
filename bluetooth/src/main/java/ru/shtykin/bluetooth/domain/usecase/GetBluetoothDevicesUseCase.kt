package ru.shtykin.bluetooth.domain.usecase

import ru.shtykin.bluetooth.Repository


class GetBluetoothDevicesUseCase (private val repository: Repository) {
    fun execute() =
        repository.getBluetoothDevices()
}
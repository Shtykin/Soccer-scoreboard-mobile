package ru.shtykin.bluetooth.domain.usecase

import ru.shtykin.bluetooth.Repository


class GetBoundedBluetoothDevicesUseCase (private val repository: Repository) {
    fun execute() =
        repository.getBoundedBluetoothDevices()
}
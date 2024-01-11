package ru.shtykin.bluetooth.domain.usecase

import ru.shtykin.bluetooth.domain.Repository


class GetBoundedBluetoothDevicesUseCase (private val repository: Repository) {
    fun execute() =
        repository.getBoundedBluetoothDevices()
}
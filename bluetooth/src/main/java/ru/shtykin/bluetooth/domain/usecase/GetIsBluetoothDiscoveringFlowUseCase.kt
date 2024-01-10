package ru.shtykin.bluetooth.domain.usecase

import ru.shtykin.bluetooth.Repository


class GetIsBluetoothDiscoveringFlowUseCase (private val repository: Repository) {
    fun execute() =
        repository.getIsBluetoothDiscoveringFlow()
}
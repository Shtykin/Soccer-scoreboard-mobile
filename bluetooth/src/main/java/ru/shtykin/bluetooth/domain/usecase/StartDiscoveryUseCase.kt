package ru.shtykin.bluetooth.domain.usecase

import ru.shtykin.bluetooth.Repository


class StartDiscoveryUseCase (private val repository: Repository) {
    fun execute() =
        repository.startDiscovery()
}
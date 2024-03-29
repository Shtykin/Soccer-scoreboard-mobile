package ru.shtykin.bluetooth.domain.usecase

import ru.shtykin.bluetooth.domain.Repository


class StartDiscoveryUseCase (private val repository: Repository) {
    fun execute() =
        repository.startDiscovery()
}
package ru.shtykin.bluetooth.domain.usecase

import ru.shtykin.bluetooth.domain.Repository


class GetSettingsFlowUseCase(private val repository: Repository) {
    fun execute() =
        repository.getSettingsFlow()
}
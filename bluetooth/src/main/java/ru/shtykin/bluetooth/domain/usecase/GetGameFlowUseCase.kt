package ru.shtykin.bluetooth.domain.usecase

import ru.shtykin.bluetooth.domain.Repository


class GetGameFlowUseCase(private val repository: Repository) {
    fun execute() =
        repository.getGameFlow()
}
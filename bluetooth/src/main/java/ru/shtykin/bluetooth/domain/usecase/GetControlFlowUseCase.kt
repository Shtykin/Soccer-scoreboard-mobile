package ru.shtykin.bluetooth.domain.usecase

import ru.shtykin.bluetooth.domain.Repository


class GetControlFlowUseCase(private val repository: Repository) {
    fun execute() =
        repository.getControlFlow()
}
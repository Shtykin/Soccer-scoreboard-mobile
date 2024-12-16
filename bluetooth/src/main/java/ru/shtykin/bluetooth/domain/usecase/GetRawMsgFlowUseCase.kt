package ru.shtykin.bluetooth.domain.usecase

import ru.shtykin.bluetooth.domain.Repository


class GetRawMsgFlowUseCase(private val repository: Repository) {
    fun execute() =
        repository.getRawMsgFlow()
}
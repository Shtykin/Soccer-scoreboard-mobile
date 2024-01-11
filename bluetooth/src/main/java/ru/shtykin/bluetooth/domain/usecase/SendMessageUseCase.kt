package ru.shtykin.bluetooth.domain.usecase

import ru.shtykin.bluetooth.domain.Repository


class SendMessageUseCase (private val repository: Repository) {
    fun execute(msg: String) =
        repository.sendMsg(msg)
}
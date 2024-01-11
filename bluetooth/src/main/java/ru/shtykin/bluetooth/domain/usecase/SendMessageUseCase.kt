package ru.shtykin.bluetooth.domain.usecase

import ru.shtykin.bluetooth.Repository
import ru.shtykin.bluetooth.domain.entity.BtDevice


class SendMessageUseCase (private val repository: Repository) {
    fun execute(msg: String) =
        repository.sendMsg(msg)
}
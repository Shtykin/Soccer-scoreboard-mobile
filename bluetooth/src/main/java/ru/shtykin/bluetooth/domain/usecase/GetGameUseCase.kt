package ru.shtykin.bluetooth.domain.usecase

import ru.shtykin.bluetooth.domain.Repository


class GetGameUseCase (private val repository: Repository) {
    fun execute() =
        repository.getGame()
}
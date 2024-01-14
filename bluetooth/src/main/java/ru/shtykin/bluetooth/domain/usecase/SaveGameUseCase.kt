package ru.shtykin.bluetooth.domain.usecase

import ru.shtykin.bluetooth.domain.Repository
import ru.shtykin.bluetooth.domain.entity.Game


class SaveGameUseCase (private val repository: Repository) {
    fun execute(game: Game) =
        repository.saveGame(game)
}
package ru.shtykin.soccerscoreboard.domain.usecase

import ru.shtykin.soccerscoreboard.domain.Repository
import ru.shtykin.soccerscoreboard.domain.entity.Game


class SaveGameUseCase (private val repository: Repository) {
    fun execute(game: Game) =
        repository.saveGame(game)
}
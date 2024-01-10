package ru.shtykin.soccerscoreboard.domain.usecase

import ru.shtykin.soccerscoreboard.domain.Repository
import ru.shtykin.soccerscoreboard.domain.entity.Game


class GetGameUseCase (private val repository: Repository) {
    fun execute() =
        repository.getGame()
}
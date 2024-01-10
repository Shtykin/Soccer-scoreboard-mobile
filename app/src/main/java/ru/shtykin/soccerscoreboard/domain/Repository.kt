package ru.shtykin.soccerscoreboard.domain

import ru.shtykin.soccerscoreboard.domain.entity.Game

interface Repository {
    fun saveGame(game: Game)
    fun getGame(): Game
}
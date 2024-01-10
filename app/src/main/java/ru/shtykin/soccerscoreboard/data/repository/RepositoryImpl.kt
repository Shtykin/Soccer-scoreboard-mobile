package ru.shtykin.soccerscoreboard.data.repository

import androidx.compose.ui.graphics.Color
import ru.shtykin.soccerscoreboard.domain.Repository
import ru.shtykin.soccerscoreboard.domain.entity.Game
import ru.shtykin.soccerscoreboard.domain.entity.Team

class RepositoryImpl(

) : Repository {

    private var game = Game(
        Team( "Команда 1", Color.Red),
        Team( "Команда 2", Color.Blue)
    )

    override fun saveGame(game: Game) {
        this.game = game
    }

    override fun getGame() = game

}
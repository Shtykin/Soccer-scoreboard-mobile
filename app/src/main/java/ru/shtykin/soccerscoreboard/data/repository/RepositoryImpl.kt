package ru.shtykin.soccerscoreboard.data.repository

import androidx.compose.ui.graphics.Color
import ru.shtykin.soccerscoreboard.domain.Repository
import ru.shtykin.soccerscoreboard.domain.entity.Game
import ru.shtykin.soccerscoreboard.domain.entity.Team

class RepositoryImpl(

) : Repository {

    private var game = Game(
        team1 = Team( "Команда 1", Color.Red),
        team2 = Team( "Команда 2", Color.Blue),
        halfTime = 30 * 60
    )

    override fun saveGame(game: Game) {
        this.game = game
    }

    override fun getGame() = game

}
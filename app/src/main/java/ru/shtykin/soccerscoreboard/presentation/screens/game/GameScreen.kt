package ru.shtykin.soccerscoreboard.presentation.screens.game

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.shtykin.bluetooth.domain.entity.Team
import ru.shtykin.soccerscoreboard.presentation.screens.settings.secondsToFormatTime
import ru.shtykin.soccerscoreboard.presentation.state.ScreenState

@Composable
fun GameScreen(
    uiState: ScreenState,
) {

    val game = (uiState as? ScreenState.GameScreen)?.game
    Log.e("DEBUG1", "g -> $game")
    game?.let {game ->

        val teams = listOf(game.team1, game.team2)

        Column(modifier = Modifier.padding(16.dp)) {
            CurrentTime(game.currentTime)

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TeamColumn(team = teams[0])
                TeamColumn(team = teams[1])
            }
        }
    }
}

@Composable
fun CurrentTime(
    time: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = time.secondsToFormatTime(),
        )
    }

}

@Composable
fun RowScope.TeamColumn(
    team: Team
) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = team.score.toString())
        Text(text = team.name)


    }

}
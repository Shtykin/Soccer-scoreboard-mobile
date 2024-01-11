package ru.shtykin.soccerscoreboard.presentation.screens.game

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExposureNeg1
import androidx.compose.material.icons.filled.ExposurePlus1
import androidx.compose.material.icons.filled.PlusOne
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.shtykin.bluetooth.domain.entity.Team
import ru.shtykin.soccerscoreboard.presentation.screens.settings.secondsToFormatTime
import ru.shtykin.soccerscoreboard.presentation.state.ScreenState
import ru.shtykin.soccerscoreboard.presentation.ui.theme.changaFontFamily

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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Time:",
                fontSize = 20.sp,
                fontFamily = changaFontFamily,
                fontWeight = FontWeight.Thin
            )
            Text(
                text = time.secondsToFormatTime(),
                fontSize = 48.sp,
                fontFamily = changaFontFamily
            )

        }
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
        Text(
            text = team.score.toString(),
            fontSize = 48.sp,
            fontFamily = changaFontFamily
        )
        Text(
            text = team.name,
            fontSize = 24.sp,
            fontFamily = changaFontFamily,
        )
        Spacer(modifier = Modifier.weight(1f))
        Card(
            modifier = Modifier
                .size(50.dp)
                .clickable{},
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = team.color)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "-1",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 24.sp,
                    fontFamily = changaFontFamily,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier
                .size(150.dp)
                .clickable{},
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = team.color)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+1",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 64.sp,
                    fontFamily = changaFontFamily,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))


    }

}
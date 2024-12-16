package ru.shtykin.soccerscoreboard.presentation.screens.game

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.shtykin.bluetooth.domain.entity.Team
import ru.shtykin.soccerscoreboard.presentation.MainViewModel
import ru.shtykin.soccerscoreboard.presentation.screens.settings.secondsToFormatTime
import ru.shtykin.soccerscoreboard.presentation.state.ScreenState
import ru.shtykin.soccerscoreboard.presentation.ui.theme.changaFontFamily

@Composable
fun GameScreen(
    uiState: ScreenState,
    viewModel: MainViewModel,
) {

    val game = (uiState as? ScreenState.GameScreen)?.game

    val control by viewModel.responseControl.collectAsState()
    val settings by viewModel.responseSettings.collectAsState()
    val data by viewModel.responseData.collectAsState()

    Log.e("DEBUG1", "g -> $game")
    game?.let {game ->

        val teams = listOf(game.team1, game.team2)

        Column(modifier = Modifier.padding(16.dp)) {
            CurrentTime(data.count)
            Card(
                onClick = {viewModel.start(!control.enable)},
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (control.enable) "Стоп" else "Старт" ,
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 32.sp,
                        fontFamily = changaFontFamily,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (!control.reverse) {
                    TeamColumn(
                        name = data.nameT1.ifEmpty { "Команда 1" },
                        score = data.scoreT1,
                        fault = data.faultT1,
                        color = if (data.colorT1 == 0) 0xFFFF0000 else data.colorT1.toLong(),
                        onPlusScoreClick = {viewModel.addScoreT1()},
                        onMinusScoreClick = {viewModel.minusScoreT1()},
                        onPlusFaultClick = {viewModel.plusFaultT1()},
                        onMinusFaultClick = {viewModel.minusFaultT1()}
                    )
                    TeamColumn(
                        name = data.nameT2.ifEmpty { "Команда 2" },
                        score = data.scoreT2,
                        fault = data.faultT2,
                        color = if (data.colorT1 == 0) 0xFF0000FF else data.colorT2.toLong(),
                        onPlusScoreClick = {viewModel.addScoreT2()},
                        onMinusScoreClick = {viewModel.minusScoreT2()},
                        onPlusFaultClick = {viewModel.plusFaultT2()},
                        onMinusFaultClick = {viewModel.minusFaultT2()}
                    )
                } else {
                    TeamColumn(
                        name = data.nameT2.ifEmpty { "Команда 2" },
                        score = data.scoreT2,
                        fault = data.faultT2,
                        color = if (data.colorT1 == 0) 0xFF0000FF else data.colorT2.toLong(),
                        onPlusScoreClick = {viewModel.addScoreT2()},
                        onMinusScoreClick = {viewModel.minusScoreT2()},
                        onPlusFaultClick = {viewModel.plusFaultT2()},
                        onMinusFaultClick = {viewModel.minusFaultT2()}
                    )
                    TeamColumn(
                        name = data.nameT1.ifEmpty { "Команда 1" },
                        score = data.scoreT1,
                        fault = data.faultT1,
                        color = if (data.colorT1 == 0) 0xFFFF0000 else data.colorT1.toLong(),
                        onPlusScoreClick = {viewModel.addScoreT1()},
                        onMinusScoreClick = {viewModel.minusScoreT1()},
                        onPlusFaultClick = {viewModel.plusFaultT1()},
                        onMinusFaultClick = {viewModel.minusFaultT1()}
                    )
                }

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
            .padding(vertical = 8.dp),
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
    name: String,
    score: Int,
    fault: Int,
    color: Long,
    onPlusScoreClick: () -> Unit,
    onMinusScoreClick: () -> Unit,
    onPlusFaultClick: () -> Unit,
    onMinusFaultClick: () -> Unit,
) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = score.toString(),
            fontSize = 48.sp,
            fontFamily = changaFontFamily
        )
        Text(
            text = name,
            fontSize = 24.sp,
            fontFamily = changaFontFamily,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(5){
                Box(modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (it >= fault) Color.LightGray else Color(0xfff99d00)))
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onMinusFaultClick) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = null)
            }
            IconButton(onClick = onPlusFaultClick) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        Card(
            onClick = onMinusScoreClick,
            modifier = Modifier
                .size(50.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(color))
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
            onClick = onPlusScoreClick,
            modifier = Modifier
                .size(150.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(color))
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
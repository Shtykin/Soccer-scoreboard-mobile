package ru.shtykin.soccerscoreboard.presentation.screens.game

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ru.shtykin.soccerscoreboard.presentation.state.ScreenState

@Composable
fun GameScreen(
    uiState: ScreenState,
) {
    Text(text = "Game")
}
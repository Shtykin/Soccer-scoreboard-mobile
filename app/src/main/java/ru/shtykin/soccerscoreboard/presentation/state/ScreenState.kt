package ru.shtykin.soccerscoreboard.presentation.state


sealed class ScreenState {

    data class SettingsScreen(
        val temp: String,
    ) : ScreenState()

    data class GameScreen(
        val temp: String,
    ) : ScreenState()

    data class DeveloperScreen(
        val temp: String,
    ) : ScreenState()
}

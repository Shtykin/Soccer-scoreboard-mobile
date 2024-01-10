package ru.shtykin.soccerscoreboard.presentation.state

import ru.shtykin.bluetooth.domain.entity.BtDevice


sealed class ScreenState {

    data class SettingsScreen(
        val boundedDevices: List<BtDevice>,
        val onlineDevices: List<BtDevice>,
        val isDiscovering: Boolean
    ) : ScreenState()

    data class GameScreen(
        val temp: String,
    ) : ScreenState()

    data class DeveloperScreen(
        val temp: String,
    ) : ScreenState()
}

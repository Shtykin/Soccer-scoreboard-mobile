package ru.shtykin.soccerscoreboard.presentation.state

import ru.shtykin.bluetooth.domain.entity.BtDevice
import ru.shtykin.soccerscoreboard.domain.entity.Game


sealed class ScreenState {

    data class SettingsScreen(
        val game: Game,
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

    data class BluetoothScreen(
        val temp: String,
    ) : ScreenState()
}

package ru.shtykin.soccerscoreboard.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsFootball
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.ui.graphics.vector.ImageVector
import ru.shtykin.bbs_mobile.navigation.Screen

sealed class MenuItem(
    val index: Int,
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Settings: MenuItem(
        index = 0,
        route = Screen.Settings.route,
        title = "Settings",
        icon = Icons.Default.Settings,
    )
    object Game: MenuItem(
        index = 1,
        route = Screen.Game.route,
        title = "Game",
        icon = Icons.Default.SportsSoccer
    )

    object Developer: MenuItem(
        index = 2,
        route = Screen.Developer.route,
        title = "Dev",
        icon = Icons.Default.DeveloperBoard
    )

    object Bluetooth: MenuItem(
        index = 0,
        route = Screen.Bluetooth.route,
        title = "Bluetooth",
        icon = Icons.Default.Bluetooth
    )
}
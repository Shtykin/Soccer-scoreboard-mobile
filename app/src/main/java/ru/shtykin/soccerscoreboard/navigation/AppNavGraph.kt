package ru.shtykin.bbs_mobile.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavGraph(
    startScreenRoute: String,
    navHostController: NavHostController,
    settingsScreenContent: @Composable () -> Unit,
    gameScreenContent: @Composable () -> Unit,
    developerScreenContent: @Composable () -> Unit,
    bluetoothScreenContent: @Composable () -> Unit,
) {
    NavHost(
        navController = navHostController,
        startDestination = startScreenRoute
    ) {
        composable(Screen.Settings.route) {
            settingsScreenContent()
        }
        composable(Screen.Game.route) {
            gameScreenContent()
        }
        composable(Screen.Developer.route) {
            developerScreenContent()
        }
        composable(Screen.Bluetooth.route) {
            bluetoothScreenContent()
        }
    }

}
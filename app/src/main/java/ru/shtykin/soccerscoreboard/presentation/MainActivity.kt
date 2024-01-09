package ru.shtykin.soccerscoreboard.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.shtykin.bbs_mobile.navigation.AppNavGraph
import ru.shtykin.bbs_mobile.navigation.Screen
import ru.shtykin.soccerscoreboard.presentation.screens.developer.DeveloperScreen
import ru.shtykin.soccerscoreboard.presentation.screens.game.GameScreen
import ru.shtykin.soccerscoreboard.presentation.screens.settings.SettingsScreen
import ru.shtykin.soccerscoreboard.presentation.ui.theme.SoccerScoreboardTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navHostController = rememberNavController()
            val uiState by viewModel.uiState
            val startScreenRoute = Screen.Settings.route
            SoccerScoreboardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavGraph(
                        startScreenRoute = startScreenRoute,
                        navHostController = navHostController,
                        settingsScreenContent = {
                            SettingsScreen(
                                uiState = uiState,
                            )
                        },
                        gameScreenContent = {
                            GameScreen(
                                uiState = uiState,
                            )
                        },
                        developerScreenContent = {
                            DeveloperScreen(
                                uiState = uiState,
                            )
                        }
                    )
                }
            }
        }
    }
}
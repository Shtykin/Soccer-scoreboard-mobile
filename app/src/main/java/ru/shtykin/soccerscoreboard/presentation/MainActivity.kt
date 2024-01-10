package ru.shtykin.soccerscoreboard.presentation

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
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

    private lateinit var btLauncher: ActivityResultLauncher<Intent>
    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBtLauncher()
        checkPermissions()
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
                                onBluetoothOnClick = {
                                    btLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                                },
                                onBoundDeviceClick = {
                                    viewModel.boundDevice(it)
                                },
                                onConnectDeviceClick = {
                                    viewModel.connectDevice(it)
                                },
                                onDisconnectClick = {
                                    viewModel.disconnectDevice()
                                },
                                onSearchClick = {
                                    viewModel.startDiscovery()
                                },
                                onSendMessageClick = {
                                    viewModel.sendMessage(it)
                                }
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

    private fun registerBtLauncher() {
        btLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                // bt on
            } else {
                // bt off
            }
        }
    }


    private fun registerPermissionLauncher() {
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { results ->
            results.forEach {
                if (!it.value) {
                    showToast("Необходимы все разрешения")
                    openAppSettings()
                }
            }
        }
    }

    private fun launchBtPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        } else {
            pLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    private fun checkPermissions() {
        if (!checkBtPermissions()) {
            registerPermissionLauncher()
            launchBtPermissions()
        }
    }

    private fun checkBtPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun openAppSettings() {
        val settingsIntent = Intent().also {
            it.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            it.addCategory(Intent.CATEGORY_DEFAULT)
            it.data = Uri.parse("package:$packageName")
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            it.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        }
        startActivity(settingsIntent)
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

}
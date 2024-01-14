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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.shtykin.bbs_mobile.navigation.AppNavGraph
import ru.shtykin.soccerscoreboard.presentation.screens.developer.DeveloperScreen
import ru.shtykin.soccerscoreboard.presentation.screens.game.GameScreen
import ru.shtykin.soccerscoreboard.presentation.screens.settings.SettingsScreen
import ru.shtykin.soccerscoreboard.presentation.state.ScreenState
import ru.shtykin.soccerscoreboard.presentation.ui.theme.SoccerScoreboardTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    private lateinit var btLauncher: ActivityResultLauncher<Intent>
    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>

    private val permissionList = when (Build.VERSION.SDK_INT) {
        in Build.VERSION_CODES.BASE..Build.VERSION_CODES.R -> {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
        Build.VERSION_CODES.S -> {
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
        else -> {
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBtLauncher()
        checkPermissions()
        setContent {
            val navHostController = rememberNavController()
            val uiState by viewModel.uiState
            var currentItem: MenuItem by remember { mutableStateOf(
                when (uiState) {
                    is ScreenState.SettingsScreen -> MenuItem.Settings
                    is ScreenState.GameScreen -> MenuItem.Game
                    is ScreenState.DeveloperScreen -> MenuItem.Developer
                }
            ) }
            val startScreenRoute = currentItem.route
            SoccerScoreboardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val itemList = listOf(
                        MenuItem.Settings,
                        MenuItem.Game,
                        MenuItem.Developer
                    )
                    Column {
                        PrimaryTabRow(selectedTabIndex = currentItem.index) {
                            itemList.forEach { item ->
                                Tab(
                                    selected = item == currentItem,
                                    onClick = {
                                        if (item != currentItem) {
                                            currentItem = item
                                            navHostController.navigate(item.route) {
                                                popUpTo(0)
                                            }
                                            when (currentItem) {
                                                is MenuItem.Game -> {
                                                    viewModel.gameScreenOpened()
                                                }

                                                is MenuItem.Settings -> {
                                                    viewModel.settingScreenOpened()
                                                }

                                                is MenuItem.Developer -> {
                                                    viewModel.developerScreenOpened()
                                                }
                                            }
                                        }
                                    },
                                    text = {
                                        Text(
                                            text = item.title,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                        }
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
                                    onColorPickedTeam = { team, color ->
                                        viewModel.changeTeamColor(team, color)
                                    },
                                    onTimeChanged = {
                                        viewModel.changeHalfTime(it)
                                    },
                                    onTeamNameChanged = { team, name ->
                                        viewModel.changeTeamName(team, name)
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
                                    onParamChange = { param, value ->
                                        viewModel.changeParamValue(param, value)
                                    }
                                )
                            },

                        )
                    }


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
        pLauncher.launch(permissionList)
    }

    private fun checkPermissions() {
        if (!checkBtPermissions()) {
            registerPermissionLauncher()
            launchBtPermissions()
        }
    }

    private fun checkBtPermissions(): Boolean {
        permissionList.forEach {
            if (ContextCompat.checkSelfPermission(
                    this,
                    it
                ) != PackageManager.PERMISSION_GRANTED) return false
        }
        return true
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
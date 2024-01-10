package ru.shtykin.soccerscoreboard.presentation.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.shtykin.bluetooth.domain.entity.BtDevice
import ru.shtykin.soccerscoreboard.presentation.state.ScreenState

@Composable
fun SettingsScreen(
    uiState: ScreenState,
    onBluetoothOnClick: (() -> Unit)?,
    onBoundDeviceClick: ((BtDevice) -> Unit)?,
    onSearchClick: (() -> Unit)?,
) {

    val boundedDevices = (uiState as? ScreenState.SettingsScreen)?.boundedDevices ?: emptyList()
    val onlineDevices = (uiState as? ScreenState.SettingsScreen)?.onlineDevices ?: emptyList()
    val isDiscovering = (uiState as? ScreenState.SettingsScreen)?.isDiscovering ?: false

    Column {
        Text(text = "Settings")
        Button(onClick = { onBluetoothOnClick?.invoke() }) {
            Text("Enable BT")
        }
//        Button(onClick = { onBoundDeviceClick?.invoke() }) {
//            Text("Get devices")
//        }
        Button(onClick = { onSearchClick?.invoke() }) {
            Text("Search")
        }
        LazyColumn {
            items(boundedDevices) {
                Text(
                    it.name,
                    color = Color.Green
                )
            }
        }
        if (isDiscovering) CircularProgressIndicator()
        LazyColumn {
            items(onlineDevices) {
                Text(
                    it.name,
                    modifier = Modifier.clickable { onBoundDeviceClick?.invoke(it) },
                    color = Color.Blue
                )
            }
        }

    }

}
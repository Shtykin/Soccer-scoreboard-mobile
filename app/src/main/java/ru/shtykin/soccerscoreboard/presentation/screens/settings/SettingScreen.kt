package ru.shtykin.soccerscoreboard.presentation.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ru.shtykin.soccerscoreboard.presentation.state.ScreenState

@Composable
fun SettingsScreen(
    uiState: ScreenState,
    onBluetoothOnClick: (() -> Unit)?,
    onGetDevicesClick: (() -> Unit)?,
    onSearchClick: (() -> Unit)?,
) {
    Column {
        Button(onClick = { onBluetoothOnClick?.invoke() }) {
            Text("Enable BT")
        }
        Button(onClick = { onGetDevicesClick?.invoke() }) {
            Text("Get devices")
        }
        Button(onClick = { onSearchClick?.invoke() }) {
            Text("Search")
        }
        Text(text = "Settings")
    }

}
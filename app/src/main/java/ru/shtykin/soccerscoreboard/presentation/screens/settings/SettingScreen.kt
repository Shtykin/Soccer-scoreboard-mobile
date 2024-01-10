package ru.shtykin.soccerscoreboard.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import io.mhssn.colorpicker.ColorPickerDialog
import io.mhssn.colorpicker.ColorPickerType
import ru.shtykin.bluetooth.domain.entity.BtDevice
import ru.shtykin.soccerscoreboard.domain.entity.Team
import ru.shtykin.soccerscoreboard.presentation.state.ScreenState

@Composable
fun SettingsScreen(
    uiState: ScreenState,
    onBluetoothClick: (() -> Unit)?,
    onBluetoothOnClick: (() -> Unit)?,
    onBoundDeviceClick: ((BtDevice) -> Unit)?,
    onConnectDeviceClick: ((BtDevice) -> Unit)?,
    onDisconnectClick: (() -> Unit)?,
    onSearchClick: (() -> Unit)?,
    onSendMessageClick: ((String) -> Unit)?,
    onColorPickedTeam: ((Team, Color) -> Unit)?
) {

    val boundedDevices = (uiState as? ScreenState.SettingsScreen)?.boundedDevices ?: emptyList()
    val onlineDevices = (uiState as? ScreenState.SettingsScreen)?.onlineDevices ?: emptyList()
    val isDiscovering = (uiState as? ScreenState.SettingsScreen)?.isDiscovering ?: false
    val game = (uiState as? ScreenState.SettingsScreen)?.game

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onBluetoothClick?.invoke() },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Состояние Bluetooth: сопряжено")
            Icon(imageVector = Icons.Default.Bluetooth, contentDescription = null)
        }

        Spacer(modifier = Modifier.height(24.dp))

        game?.let {game ->
            Row {
                TeamSettings(
                    team = game.team1,
                    onColorPicked = {color ->
                        onColorPickedTeam?.invoke(game.team1, color)
                    }
                )
                TeamSettings(
                    team = game.team2,
                    onColorPicked = {color ->
                        onColorPickedTeam?.invoke(game.team2, color)
                    }
                )
            }
        }

        //цвета, время тайма, название команд
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { onBluetoothOnClick?.invoke() }) {
            Text("Enable BT")
        }
        Button(onClick = { onSearchClick?.invoke() }) {
            Text("Search")
        }
        Button(onClick = { onDisconnectClick?.invoke() }) {
            Text("Disconnect")
        }
        Button(onClick = { onSendMessageClick?.invoke("H") }) {
            Text(text = "Send")
        }
        LazyColumn {
            items(boundedDevices) {
                Text(
                    it.name,
                    modifier = Modifier.clickable { onConnectDeviceClick?.invoke(it) },
                    color = Color.Green
                )
            }
        }

        LazyColumn {
            items(onlineDevices) {
                Text(
                    it.name,
                    modifier = Modifier.clickable { onBoundDeviceClick?.invoke(it) },
                    color = Color.Blue
                )
            }
        }
        if (isDiscovering) CircularProgressIndicator()

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RowScope.TeamSettings(
    team: Team,
    onColorPicked: ((Color) -> Unit)?,
) {
    var showPickerDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(color = team.color, shape = RectangleShape)
                .clickable {
                    showPickerDialog = true
                }
        ) {
        }
        Text(
            text = team.name
        )
    }

    ColorPickerDialog(
        show = showPickerDialog,
        type = colorPickerType,
        properties = DialogProperties(),
        onDismissRequest = {
            showPickerDialog = false
        },
        onPickedColor = {
            showPickerDialog = false
            onColorPicked?.invoke(it)
        },
    )

}

val colorPickerType = ColorPickerType.Circle(
    showBrightnessBar = true,
    showAlphaBar = false,
    lightCenter = true
)

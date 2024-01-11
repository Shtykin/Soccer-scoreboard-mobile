package ru.shtykin.soccerscoreboard.presentation.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
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
    onColorPickedTeam: ((Team, Color) -> Unit)?,
    onTimeChanged: ((Int) -> Unit)?,
    onTeamNameChanged: ((Team, String) -> Unit)?,
) {

    val boundedDevices = (uiState as? ScreenState.SettingsScreen)?.boundedDevices ?: emptyList()
    val onlineDevices = (uiState as? ScreenState.SettingsScreen)?.onlineDevices ?: emptyList()
    val isDiscovering = (uiState as? ScreenState.SettingsScreen)?.isDiscovering ?: false
    val game = (uiState as? ScreenState.SettingsScreen)?.game


    var minutes by remember { mutableStateOf(0) }
    var seconds by remember { mutableStateOf(0) }
    var showTimePickerDialog by remember { mutableStateOf(false) }


    TimePickerDialog(
        show = showTimePickerDialog,
        onDismissRequest = { showTimePickerDialog = false },
        onSaveClick = {
            onTimeChanged?.invoke(minutes * 60 + seconds)
            showTimePickerDialog = false
        },
        onPickMinutes = { minutes = it },
        onPickSeconds = { seconds = it }
    )

    Column(modifier = Modifier.padding(16.dp)) {
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

        game?.let { game ->
            Text(
                "Время тайма: ${game.halfTime.secondsToFormatTime()}",
                modifier = Modifier.clickable { showTimePickerDialog = true }
            )
            Row {
                TeamSettings(
                    team = game.team1,
                    onColorPicked = { color ->
                        onColorPickedTeam?.invoke(game.team1, color)
                    },
                    onNameChanged = {newName ->
                        onTeamNameChanged?.invoke(game.team1, newName)
                    }
                )
                TeamSettings(
                    team = game.team2,
                    onColorPicked = { color ->
                        onColorPickedTeam?.invoke(game.team2, color)
                    },
                    onNameChanged = {newName ->
                        onTeamNameChanged?.invoke(game.team2, newName)
                    }
                )
            }
        }


        //цвета, время тайма, название команд
//        Spacer(modifier = Modifier.height(24.dp))
//        Button(onClick = { onBluetoothOnClick?.invoke() }) {
//            Text("Enable BT")
//        }
//        Button(onClick = { onSearchClick?.invoke() }) {
//            Text("Search")
//        }
//        Button(onClick = { onDisconnectClick?.invoke() }) {
//            Text("Disconnect")
//        }
//        Button(onClick = { onSendMessageClick?.invoke("H") }) {
//            Text(text = "Send")
//        }
//        LazyColumn {
//            items(boundedDevices) {
//                Text(
//                    it.name,
//                    modifier = Modifier.clickable { onConnectDeviceClick?.invoke(it) },
//                    color = Color.Green
//                )
//            }
//        }
//
//        LazyColumn {
//            items(onlineDevices) {
//                Text(
//                    it.name,
//                    modifier = Modifier.clickable { onBoundDeviceClick?.invoke(it) },
//                    color = Color.Blue
//                )
//            }
//        }
//        if (isDiscovering) CircularProgressIndicator()

    }
}

@Composable
fun TeamNameDialog(
    show: Boolean,
    modifier: Modifier = Modifier,
    name: String,
    onDismissRequest: () -> Unit,
    onSaveClick: (String) -> Unit,

    ) {
    var teamName by remember {
        mutableStateOf(name)
    }
    if (show) {
        AlertDialog(
            onDismissRequest = {
                onDismissRequest.invoke()
            },
            title = {
                Text(
                    text = "Установка названия команды",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                TextField(value = teamName, onValueChange = { teamName = it })
            },
            dismissButton = {
                TextButton(onClick = {
                    onDismissRequest.invoke()
                }) {
                    Text(text = "Отмена")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onSaveClick.invoke(teamName)
                }) {
                    Text(text = "Сохранить")
                }

            },
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RowScope.TeamSettings(
    team: Team,
    onColorPicked: ((Color) -> Unit)?,
    onNameChanged: ((String) -> Unit)?,
) {
    var showColorPickerDialog by remember { mutableStateOf(false) }
    var showTeamNameDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            modifier = Modifier.size(64.dp),
            onClick = { showColorPickerDialog = true }
        ) {
            Icon(
                imageVector = Icons.Default.Groups,
                modifier = Modifier.size(64.dp),
                contentDescription = null,
                tint = team.color
            )
        }

        Text(
            modifier = Modifier.clickable { showTeamNameDialog = true },
            text = team.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }

    ColorPickerDialog(
        show = showColorPickerDialog,
        type = colorPickerType,
        properties = DialogProperties(),
        onDismissRequest = {
            showColorPickerDialog = false
        },
        onPickedColor = {
            showColorPickerDialog = false
            onColorPicked?.invoke(it)
        },
    )
    TeamNameDialog(
        show = showTeamNameDialog,
        name = team.name,
        onDismissRequest = { showTeamNameDialog = false },
        onSaveClick = {
            showTeamNameDialog = false
            onNameChanged?.invoke(it)
        }
    )

}

@Composable
fun TimePickerDialog(
    show: Boolean,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onSaveClick: () -> Unit,
    onPickMinutes: (Int) -> Unit,
    onPickSeconds: (Int) -> Unit,

    ) {
//    var showDialog by remember(show) {
//        mutableStateOf(show)
//    }


    if (show) {
        AlertDialog(
            onDismissRequest = {
                onDismissRequest.invoke()
//                showDialog = false
            },
            title = {
                Text(
                    text = "Установка времени тайма",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TimeNumberPicker(
                        name = "мин.",
                        numbers = (0..60).toList()
                    ) {
                        onPickMinutes.invoke(it)
                    }
                    Spacer(modifier = Modifier.width(20.dp))

                    TimeNumberPicker(
                        name = "сек.",
                        numbers = (0..59).toList()
                    ) {
                        onPickSeconds.invoke(it)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onDismissRequest.invoke()
//                    showDialog = false
                }) {
                    Text(text = "Отмена")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onSaveClick.invoke()
//                    showDialog = false
                }) {
                    Text(text = "Сохранить")
                }

            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T : Number> TimeNumberPicker(
    name: String,
    numbers: List<T>,
    onPickNumber: (T) -> Unit
) {
    val hoursListState = rememberLazyListState()
    val hoursSnapFling = rememberSnapFlingBehavior(lazyListState = hoursListState)
    var hoursLayoutSize by remember { mutableStateOf(Size.Zero) }
    Column {
        Card(
            shape = RectangleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
        ) {
            LazyColumn(
                flingBehavior = hoursSnapFling,
                modifier = Modifier
                    .width(80.dp)
                    .height(100.dp)
                    .onSizeChanged { hoursLayoutSize = it.toSize() },
                state = hoursListState,
                contentPadding = PaddingValues(0.dp, 36.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(numbers.toList()) { hour ->
                    var positionRatio by remember { mutableStateOf(0f) }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .onGloballyPositioned { coordinates ->
                                coordinates
                                    .boundsInParent()
                                    .run {
                                        val x =
                                            (((top + (height / 2)) / (hoursLayoutSize.height)) - .5f) * 2
                                        positionRatio = x * x
                                        if (top > 0 && bottom < hoursLayoutSize.height) onPickNumber(
                                            hour
                                        )
                                    }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$hour",
                            textAlign = TextAlign.Center,
                            fontSize = (28 - 14 * positionRatio).sp,
                        )
                    }
                }
            }
        }
        Text(
            name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.tertiary

        )
    }

}

val colorPickerType = ColorPickerType.Circle(
    showBrightnessBar = true,
    showAlphaBar = false,
    lightCenter = true
)

fun Int.secondsToFormatTime(): String {
    val seconds = this % 60
    val minutes = this / 60
    val stringSeconds = if (seconds >= 10) "$seconds" else "0$seconds"
    val stringMinutes = if (minutes >= 10) "$minutes" else "0$minutes"
    return "$stringMinutes : $stringSeconds"
}

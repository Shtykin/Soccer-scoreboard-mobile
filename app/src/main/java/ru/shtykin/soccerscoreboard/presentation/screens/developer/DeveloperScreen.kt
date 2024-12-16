package ru.shtykin.soccerscoreboard.presentation.screens.developer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.shtykin.bluetooth.domain.entity.DevParam
import ru.shtykin.soccerscoreboard.presentation.MainViewModel
import ru.shtykin.soccerscoreboard.presentation.state.ScreenState
import ru.shtykin.soccerscoreboard.presentation.ui.theme.changaFontFamily

@Composable
fun DeveloperScreen(
    uiState: ScreenState,
    viewModel: MainViewModel,
    onParamChange: (DevParam, String) -> Unit
) {
//    val devParams: List = (uiState as? ScreenState.DeveloperScreen)?.devParams ?: emptyList()
    val settings by viewModel.responseSettings.collectAsState()
    
    
    
    LazyColumn{
        item {
            DevItem(
                name = "Переключение счета",
                value = if (settings.scoreShow) "1" else "0",
                onParamChange = {viewModel.setScoreShow(it)}
            )
        }
        item {
            DevItem(
                name = "Время переключения счета",
                value = settings.scoreTime.toString(),
                onParamChange = {viewModel.setScoreTime(it)}
            )
        }
        item {
            DevItem(
                name = "Длительность счета",
                value = settings.scoreDuration.toString(),
                onParamChange = {viewModel.setScoreDuration(it)}
            )
        }
        item {
            DevItem(
                name = "Цвет таймера",
                value = settings.colorCount.toString(),
                onParamChange = {viewModel.setColorCount(it)}
            )
        }
        item {
            DevItem(
                name = "Цвет счет",
                value = settings.colorScore.toString(),
                onParamChange = {viewModel.setColorScore(it)}
            )
        }
        item {
            DevItem(
                name = "Цвет фолы",
                value = settings.colorFault.toString(),
                onParamChange = {viewModel.setColorFault(it)}
            )
        }
        item {
            DevItem(
                name = "Яркость таймера",
                value = settings.brightnessCount.toString(),
                onParamChange = {viewModel.setBrightnessCount(it)}
            )
        }
        item {
            DevItem(
                name = "Яркость фолы",
                value = settings.brightnessFault.toString(),
                onParamChange = {viewModel.setBrightnessFault(it)}
            )
        }
        item {
            DevItem(
                name = "Яркость счет",
                value = settings.brightnessScore.toString(),
                onParamChange = {viewModel.setBrightnessScore(it)}
            )
        }
    }
}

@Composable
fun DevItem(
    name: String,
    value: String,
    onParamChange: (Int) -> Unit
) {
    var showChangeParamDialog by remember { mutableStateOf(false) }
    ChangeParamDialog(
        show = showChangeParamDialog,
        name = name,
        initValue = value,
        onDismissRequest = {showChangeParamDialog = false},
        onSaveClick = {
            onParamChange.invoke(it)
            showChangeParamDialog = false
        }
    )
    Card(
        modifier = Modifier.clickable { showChangeParamDialog = true },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RectangleShape,
    ) {
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = name,
                fontSize = 20.sp,
                fontFamily = changaFontFamily,
                fontWeight = FontWeight.Thin
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = value,
                fontSize = 20.sp,
                fontFamily = changaFontFamily,
                fontWeight = FontWeight.Thin
            )
        }
        HorizontalDivider()
    }
}

@Composable
fun ChangeParamDialog(
    show: Boolean,
    name: String,
    initValue: String,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onSaveClick: (Int) -> Unit,
) {
    var value by remember {
        mutableStateOf(initValue)
    }
    if (show) {
        AlertDialog(
            onDismissRequest = { onDismissRequest.invoke() },
            title = {
                Text(
                    text = name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                TextField(
                    value = value,
                    onValueChange = { value = it.filter { char -> char.isDigit() } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            dismissButton = {
                TextButton(onClick = { onDismissRequest.invoke() }) {
                    Text(text = "Отмена")
                }
            },
            confirmButton = {
                TextButton(onClick = { onSaveClick.invoke(value.toInt()) }) {
                    Text(text = "Сохранить")
                }
            },
        )
    }
}
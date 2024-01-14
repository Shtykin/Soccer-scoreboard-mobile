package ru.shtykin.soccerscoreboard.presentation.screens.developer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.shtykin.bluetooth.domain.entity.DevParam
import ru.shtykin.soccerscoreboard.presentation.state.ScreenState
import ru.shtykin.soccerscoreboard.presentation.ui.theme.changaFontFamily

@Composable
fun DeveloperScreen(
    uiState: ScreenState,
    onParamChange: (DevParam, String) -> Unit
) {
    val devParams = (uiState as? ScreenState.DeveloperScreen)?.devParams ?: emptyList()

    LazyColumn{
        items(devParams) { param ->
            DevItem(
                param = param,
                onParamChange = {onParamChange.invoke(param, it)}
            )
        }
    }
}

@Composable
fun DevItem(
    param: DevParam,
    onParamChange: (String) -> Unit
) {
    var showChangeParamDialog by remember { mutableStateOf(false) }
    ChangeParamDialog(
        show = showChangeParamDialog,
        param = param,
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
                text = param.name,
                fontSize = 20.sp,
                fontFamily = changaFontFamily,
                fontWeight = FontWeight.Thin
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = param.value,
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
    param: DevParam,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onSaveClick: (String) -> Unit,
) {
    var value by remember {
        mutableStateOf(param.value)
    }
    if (show) {
        AlertDialog(
            onDismissRequest = { onDismissRequest.invoke() },
            title = {
                Text(
                    text = param.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                TextField(value = value, onValueChange = { value = it })
            },
            dismissButton = {
                TextButton(onClick = { onDismissRequest.invoke() }) {
                    Text(text = "Отмена")
                }
            },
            confirmButton = {
                TextButton(onClick = { onSaveClick.invoke(value) }) {
                    Text(text = "Сохранить")
                }
            },
        )
    }
}
package ru.shtykin.bluetooth.domain.entity

import androidx.compose.ui.graphics.Color

data class Team(
    var name: String,
    var color: Color,
    val score: Int = 0
)

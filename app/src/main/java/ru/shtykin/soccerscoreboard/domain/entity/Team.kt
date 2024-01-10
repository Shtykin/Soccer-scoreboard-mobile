package ru.shtykin.soccerscoreboard.domain.entity

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class Team(
    val name: String,
    var color: Color,
    val score: Int = 0
)

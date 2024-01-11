package ru.shtykin.bluetooth.domain.entity

data class Game(
    val team1: Team,
    val team2: Team,
    val halfTime: Int,
    val currentTime: Int
)

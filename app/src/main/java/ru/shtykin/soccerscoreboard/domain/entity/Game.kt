package ru.shtykin.soccerscoreboard.domain.entity

data class Game(
    val team1: Team,
    val team2: Team,
    var halfTime: Int
)

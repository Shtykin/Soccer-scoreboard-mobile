package ru.shtykin.bluetooth.domain.entity.response

import kotlinx.serialization.Serializable

@Serializable
data class ResponseSettings(
    val scoreShow: Boolean,
    val scoreTime: Int,
    val scoreDuration: Int,
    val colorCount: Int,
    val colorScore: Int,
    val colorFault: Int,
    val brightnessCount: Int,
    val brightnessFault: Int,
    val brightnessScore: Int,
    val brightnessTeam: Int,
)
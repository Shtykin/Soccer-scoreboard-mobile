package ru.shtykin.bluetooth.domain.entity.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestSettings(
    val scoreShow: Boolean? = null,
    val scoreTime: Int? = null,
    val scoreDuration: Int? = null,
    val colorCount: Int? = null,
    val colorScore: Int? = null,
    val colorFault: Int? = null,
    val brightnessCount: Int? = null,
    val brightnessFault: Int? = null,
    val brightnessScore: Int? = null,
    val brightnessTeam: Int? = null,
)

package ru.shtykin.bluetooth.domain.entity.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseMessageSettings(
    @SerialName("settings") val responseSettings: ResponseSettings
)

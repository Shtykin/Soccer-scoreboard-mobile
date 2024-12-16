package ru.shtykin.bluetooth.domain.entity.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestMessageControl(
    @SerialName("control") val requestControl: RequestControl
)


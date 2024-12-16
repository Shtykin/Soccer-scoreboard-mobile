package ru.shtykin.bluetooth.domain.entity.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.shtykin.bluetooth.domain.entity.request.RequestControl

@Serializable
data class ResponseMessageControl(
    @SerialName("control") val responseControl: ResponseControl
)

package ru.shtykin.bluetooth.domain.entity.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestMessageData(
    @SerialName("data") val requestData: RequestData
)

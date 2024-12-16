package ru.shtykin.bluetooth.domain.entity.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.shtykin.bluetooth.domain.entity.request.RequestData

@Serializable
data class ResponseMessageData(
    @SerialName("data") val responseData: ResponseData
)

package ru.shtykin.bluetooth.domain.entity.response

import kotlinx.serialization.Serializable

@Serializable
data class ResponseControl(
    val enable: Boolean,
    val reset: Boolean,
    val reverse: Boolean,
)

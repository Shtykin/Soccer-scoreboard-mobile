package ru.shtykin.bluetooth.domain.entity.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestControl(
    val enable: Boolean? = null,
    val reset: Boolean? = null,
    val reverse: Boolean? = null,
)

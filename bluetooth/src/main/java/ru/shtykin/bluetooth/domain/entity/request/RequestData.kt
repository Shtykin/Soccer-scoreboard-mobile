package ru.shtykin.bluetooth.domain.entity.request

import kotlinx.serialization.Serializable

@Serializable
data class RequestData(
    val count: Int? = null,
    val fullTime: Int? = null,
    val nameT1: String? = null,
    val nameT2: String? = null,
    val colorT1: Int? = null,
    val colorT2: Int? = null,
    val scoreT1: Int? = null,
    val scoreT2: Int? = null,
    val faultT1: Int? = null,
    val faultT2: Int? = null,
)

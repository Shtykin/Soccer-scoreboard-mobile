package ru.shtykin.bluetooth.domain.entity.response

import kotlinx.serialization.Serializable

@Serializable
data class ResponseData(
    val count: Int ,
    val fullTime: Int,
    val nameT1: String,
    val nameT2: String,
    val colorT1: Int,
    val colorT2: Int,
    val scoreT1: Int,
    val scoreT2: Int,
    val faultT1: Int,
    val faultT2: Int,
)

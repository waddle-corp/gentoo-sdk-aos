package com.waddle.gentoo.internal.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FloatingCommentResponse(
    @SerialName("this")
    val commentForThis: String,
    @SerialName("needs")
    val commentForNeeds: String,
    val case: String
)

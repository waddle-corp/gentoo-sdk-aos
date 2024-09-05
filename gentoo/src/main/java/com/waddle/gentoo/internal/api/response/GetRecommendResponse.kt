package com.waddle.gentoo.internal.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetRecommendResponse(
    @SerialName("this")
    val thisDescription: String,
    val needs: String,
    val case: String
)

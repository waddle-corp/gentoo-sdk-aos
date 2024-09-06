package com.waddle.gentoo.internal.api.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val statusCode: Int,
    val error: String
)

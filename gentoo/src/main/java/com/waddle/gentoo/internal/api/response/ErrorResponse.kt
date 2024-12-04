package com.waddle.gentoo.internal.api.response

internal data class ErrorResponse(
    val statusCode: Int,
    val body: String
)

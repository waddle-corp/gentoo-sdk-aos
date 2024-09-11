package com.waddle.gentoo.internal.api.response

import kotlinx.serialization.Serializable


@Serializable
data class AuthResponse(
    val statusCode: Int,
    val body: AuthResponseBody
)

@Serializable
data class AuthResponseBody(
    val randomId: String
)

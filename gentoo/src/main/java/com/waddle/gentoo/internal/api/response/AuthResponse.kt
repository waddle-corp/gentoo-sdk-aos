package com.waddle.gentoo.internal.api.response

import kotlinx.serialization.Serializable


@Serializable
internal data class AuthResponse(
//    val statusCode: Int,
//    val body: AuthResponseBody
    val randomId: String
)

@Serializable
internal data class AuthResponseBody(
    val randomId: String
)

internal data class AuthInfo(
    val udid: String,
    val authCode: String,
    val authResponse: AuthResponse
)

package com.waddle.gentoo.internal.api.response

import kotlinx.serialization.Serializable


@Serializable
data class AuthResponse(
//    val statusCode: Int,
//    val body: AuthResponseBody
    val randomId: String
)

@Serializable
data class AuthResponseBody(
    val randomId: String
)

data class AuthInfo(
    val udid: String,
    val authCode: String,
    val authResponse: AuthResponse
)

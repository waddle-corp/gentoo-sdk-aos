package com.waddle.gentoo.internal.api.response

import kotlinx.serialization.Serializable


@Serializable
internal data class AuthResponse(
    val chatUserId: String
)

internal data class AuthInfo(
    val udid: String,
    val userToken: String,
    val authResponse: AuthResponse
)

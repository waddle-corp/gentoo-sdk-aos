package com.waddle.gentoo.internal.api.response

import kotlinx.serialization.Serializable

@Serializable
internal data class UserEventResponse(
    val message: String
)

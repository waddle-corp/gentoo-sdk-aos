package com.waddle.gentoo.internal.api.response

import kotlinx.serialization.Serializable

@Serializable
internal data class UserEventResponse(
    val partnerId: String,
    val eventType: String,
    val visitorId: String,
    val channelId: String
)

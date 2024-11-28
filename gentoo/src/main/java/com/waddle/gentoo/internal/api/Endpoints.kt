package com.waddle.gentoo.internal.api

internal object Endpoints {
    private const val BASE = "/api/v1"
    const val USER = "$BASE/user"
    const val FLOATING = "$BASE/chat/floating/%s"
    const val USER_EVENT = "/userEvent"
}

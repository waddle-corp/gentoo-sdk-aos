package com.waddle.gentoo.internal.api

internal object Endpoints {
    private const val BASE = "/api/v1"
    const val USER = "$BASE/user"
    const val RECOMMEND = "/%s/recommend"
    const val USER_EVENT = "/userEvent"
}

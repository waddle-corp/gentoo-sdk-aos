package com.waddle.gentoo.internal.api.response

import kotlinx.serialization.Serializable

@Serializable
internal data class FloatingComment(
    val imageUrl: String,
    val comment: String
)

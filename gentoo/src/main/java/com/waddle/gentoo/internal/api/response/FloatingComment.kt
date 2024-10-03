package com.waddle.gentoo.internal.api.response

import kotlinx.serialization.Serializable

@Serializable
internal data class FloatingComment(
    val message: String,
    val case: String
)

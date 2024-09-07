package com.waddle.gentoo.internal.util

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

internal val String.urlEncoded: String
    get() {
        return try {
            URLEncoder.encode(this, StandardCharsets.UTF_8.toString())
        } catch (e: Exception) {
            this
        }
    }

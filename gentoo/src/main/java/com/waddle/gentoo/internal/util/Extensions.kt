package com.waddle.gentoo.internal.util

import android.content.Context
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

internal fun Int.toDp(context: Context): Int {
    return (this * context.resources.displayMetrics.density).toInt()
}

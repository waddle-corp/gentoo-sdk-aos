package com.waddle.gentoo.internal.api

import com.waddle.gentoo.internal.util.GentooResponse

internal fun interface ResponseHandler<T> {
    fun onResponse(response: GentooResponse<T>)
}

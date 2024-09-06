package com.waddle.gentoo.internal.api

internal fun interface ResponseHandler<T> {
    fun onResponse(response: GentooResponse<T>)
}

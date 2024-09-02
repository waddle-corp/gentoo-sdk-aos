package com.waddle.gentoo.internal.api

import com.waddle.gentoo.internal.util.Response

internal interface ResponseHandler<T> {
    fun onResponse(response: Response<T>)
}

package com.waddle.gentoo.internal.api

import com.waddle.gentoo.internal.api.response.ErrorResponse
import com.waddle.gentoo.internal.exception.GentooException

sealed class GentooResponse<out A> {
    val success: A?
        get() = (this as? Success)?.value

    val exception: GentooException?
        get() = (this as? Failure)?.e

    val error: ErrorResponse?
        get() = (this as? Failure)?.errorResponse

    data class Success<out A>(val value: A) : GentooResponse<A>()
    data class Failure(
        val errorResponse: ErrorResponse,
        val e: GentooException? = null
    ) : GentooResponse<Nothing>()
}

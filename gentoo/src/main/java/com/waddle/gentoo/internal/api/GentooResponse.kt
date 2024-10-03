package com.waddle.gentoo.internal.api

import com.waddle.gentoo.internal.api.response.ErrorResponse

internal sealed class GentooResponse<out A> {
    val success: A?
        get() = (this as? Success)?.value

    val error: ErrorResponse?
        get() = (this as? Failure)?.errorResponse

    data class Success<out A>(val value: A) : GentooResponse<A>()
    data class Failure(
        val errorResponse: ErrorResponse,
    ) : GentooResponse<Nothing>()
}

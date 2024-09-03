package com.waddle.gentoo.internal.util

import com.waddle.gentoo.internal.exception.GentooException

sealed class GentooResponse<out A> {
    val success: A?
        get() = (this as? Success)?.value

    val exception: GentooException?
        get() = (this as? Failure)?.e

    data class Success<out A>(val value: A) : GentooResponse<A>()
    data class Failure(val e: GentooException) : GentooResponse<Nothing>()
}

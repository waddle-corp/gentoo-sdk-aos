package com.waddle.gentoo.internal.exception

class GentooException(
    message: String?,
    throwable: Throwable?
) : Exception(message, throwable) {
    constructor(e: Exception) : this(e.message, e)
    constructor(message: String) : this(message, null)
}

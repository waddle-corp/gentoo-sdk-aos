package com.waddle.gentoo

import android.util.Log

internal object Logger {
    private const val TAG = "Gentoo"

    internal var loggerLevel: LogLevel = LogLevel.NONE

    fun e(message: String) {
        if (loggerLevel.level >= LogLevel.ERROR.level) {
            Log.e(TAG, message)
        }
    }

    fun w(message: String) {
        if (loggerLevel.level >= LogLevel.WARNING.level) {
            Log.w(TAG, message)
        }
    }

    fun i(message: String) {
        if (loggerLevel.level >= LogLevel.INFO.level) {
            Log.i(TAG, message)
        }
    }

    fun d(message: String) {
        if (loggerLevel.level >= LogLevel.DEBUG.level) {
            Log.d(TAG, message)
        }
    }
}

enum class LogLevel(internal val level: Int) {
    ERROR(0),
    WARNING(1),
    INFO(2),
    DEBUG(3),
    NONE(-1),
}

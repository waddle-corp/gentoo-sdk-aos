package com.waddle.gentoo

import android.util.Log

internal object Logger {
    private const val TAG = "Gentoo"

    internal var loggerLevel: LoggerLevel = LoggerLevel.NONE

    fun e(message: String) {
        if (loggerLevel.level >= LoggerLevel.ERROR.level) {
            Log.e(TAG, message)
        }
    }

    fun w(message: String) {
        if (loggerLevel.level >= LoggerLevel.WARNING.level) {
            Log.w(TAG, message)
        }
    }

    fun i(message: String) {
        if (loggerLevel.level >= LoggerLevel.INFO.level) {
            Log.i(TAG, message)
        }
    }

    fun d(message: String) {
        if (loggerLevel.level >= LoggerLevel.DEBUG.level) {
            Log.d(TAG, message)
        }
    }
}

enum class LoggerLevel(internal val level: Int) {
    ERROR(0),
    WARNING(1),
    INFO(2),
    DEBUG(3),
    NONE(-1),
}

package com.waddle.gentoo.internal.util

import com.waddle.gentoo.BuildConfig
import kotlinx.serialization.json.Json

internal object Constants {
    val MAIN_SERVER_URL: String
        get() = if (BuildConfig.DEBUG) {
            "https://dev-api.gentooai.com/chat"
        } else {
            "https://api.gentooai.com/chat"
        }

    val USER_EVENT_SERVER_URL: String
        get() = if (BuildConfig.DEBUG) {
            "https://7u6bc0lsf4.execute-api.ap-northeast-2.amazonaws.com"
        } else {
            "https://9rqj026gwg.execute-api.ap-northeast-2.amazonaws.com"
        }

    val json = Json { ignoreUnknownKeys = true }
}

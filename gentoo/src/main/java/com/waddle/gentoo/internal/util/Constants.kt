package com.waddle.gentoo.internal.util

import com.waddle.gentoo.BuildConfig

internal object Constants {
    val API_KEY: String
        get() = if (BuildConfig.DEBUG) {
            "G4J2wPnd643wRoQiK52PO9ZAtaD6YNCAhGlfm1Oc"
        } else {
            "EYOmgqkSmm55kxojN6ck7a4SKlvKltpd9X5r898k"
        }

    val BASE_URL: String
        get() = if (BuildConfig.DEBUG) {
            "https://hg5eey52l4.execute-api.ap-northeast-2.amazonaws.com/dev"
        } else {
            "https://byg7k8r4gi.execute-api.ap-northeast-2.amazonaws.com/prod"
        }
}
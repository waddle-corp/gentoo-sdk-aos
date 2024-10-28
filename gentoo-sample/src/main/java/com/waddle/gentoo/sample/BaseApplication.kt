package com.waddle.gentoo.sample

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.waddle.gentoo.Gentoo
import com.waddle.gentoo.InitializeParams
import com.waddle.gentoo.LogLevel

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Gentoo.initialize(
            InitializeParams(
                clientId = "dlst",
                authCode = "test",
                udid = "test"
            )
        )
        Gentoo.logLevel = LogLevel.DEBUG
    }
}

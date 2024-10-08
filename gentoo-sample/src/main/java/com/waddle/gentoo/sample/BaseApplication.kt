package com.waddle.gentoo.sample

import android.app.Application
import com.waddle.gentoo.Gentoo
import com.waddle.gentoo.InitializeParams
import com.waddle.gentoo.LogLevel

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
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

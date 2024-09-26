package com.waddle.gentoo.ui

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient

class GentooChatView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {
    init {
        this.settings.javaScriptEnabled = true
        this.settings.domStorageEnabled = true

        webViewClient = WebViewClient()
        webChromeClient = WebChromeClient()
    }
}

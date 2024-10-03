package com.waddle.gentoo.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient

class GentooChatView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {
    var onInputFocused: () -> Unit = {}
    init {
        this.settings.javaScriptEnabled = true
        this.settings.domStorageEnabled = true
        this.focusable = FOCUSABLE_AUTO
        isFocusableInTouchMode = true

        webViewClient = WebViewClient()
        webChromeClient = WebChromeClient()

        this.addJavascriptInterface(WebAppInterface(), "Android")
    }

    inner class WebAppInterface {
        @JavascriptInterface
        fun sendInputFocusState(message: String) {
            if (message == "input focused") {
                onInputFocused()
            }
        }
    }
}

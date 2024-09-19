package com.waddle.gentoo.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.waddle.gentoo.databinding.ActivityGentooChatBinding

class GentooChatActivity : AppCompatActivity() {
    lateinit var binding: ActivityGentooChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGentooChatBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val chatUrl = intent.getStringExtra(INTENT_CHAT_URL)
        if (chatUrl == null) {
            finish()
            return
        }

        binding.gentooChatView.loadUrl(chatUrl)
    }

    companion object {
        const val INTENT_CHAT_URL = "gentoo_chat_url"
    }
}


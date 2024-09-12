package com.waddle.gentoo.sample

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.waddle.gentoo.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        viewModel.chatUrl.observe(this) {
            if (it != null) {
                binding.gentooFloatingActionButton.chatUrl = it
            }
        }

        viewModel.floatingComment.observe(this) {
            binding.gentooFloatingActionButton.setFloatingComment(it)
        }

        viewModel.fetchFloatingComment()
        viewModel.fetchChatUrl()
    }
}

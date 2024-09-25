package com.waddle.gentoo.sample

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.waddle.gentoo.Gentoo
import com.waddle.gentoo.sample.databinding.ActivityMainBinding
import com.waddle.gentoo.viewmodel.GentooDetailViewModel
import com.waddle.gentoo.viewmodel.GentooDetailViewModelFactory
import com.waddle.gentoo.viewmodel.GentooHomeViewModel

class MainActivity : AppCompatActivity() {
    val viewModel: GentooHomeViewModel by viewModels()
    val detailViewMode: GentooDetailViewModel by viewModels {
        GentooDetailViewModelFactory("3190")
    }

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        Gentoo.bind(binding.gentooFloatingActionButton, viewModel, lifecycleScope)
        Gentoo.bind(binding.gentooDetailFloatingActionButton, detailViewMode, lifecycleScope)
    }
}

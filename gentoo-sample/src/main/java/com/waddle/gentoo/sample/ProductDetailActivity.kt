package com.waddle.gentoo.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.waddle.gentoo.Gentoo
import com.waddle.gentoo.sample.databinding.ActivityProductDetailBinding
import com.waddle.gentoo.viewmodel.GentooProductDetailViewModel
import com.waddle.gentoo.viewmodel.GentooProductDetailViewModelFactory

class ProductDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityProductDetailBinding
    val viewModel: GentooProductDetailViewModel by viewModels {
        val itemId = intent.getStringExtra(EXTRA_ITEM_ID) ?: ""
        GentooProductDetailViewModelFactory(itemId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProductDetailBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.topBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.topBar.setNavigationIconTint(ContextCompat.getColor(this, R.color.white))

        Gentoo.bind(binding.gentooDetailFloatingActionButton, viewModel, lifecycleScope)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_ITEM_ID = "EXTRA_ITEM_ID"
    }
}
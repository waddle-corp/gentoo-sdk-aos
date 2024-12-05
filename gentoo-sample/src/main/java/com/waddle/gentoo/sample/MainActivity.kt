package com.waddle.gentoo.sample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.waddle.gentoo.Gentoo
import com.waddle.gentoo.internal.api.request.Product
import com.waddle.gentoo.internal.api.request.UserEvent
import com.waddle.gentoo.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.homeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.productListButton.setOnClickListener {
            val intent = Intent(this, ProductListActivity::class.java)
            startActivity(intent)
        }

        binding.detailButton.setOnClickListener {
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra(ProductDetailActivity.EXTRA_ITEM_ID, "3190") // replace hard-coded item id to selected item id
            startActivity(intent)
        }

        binding.purchaseCompleteButton.setOnClickListener {
            Gentoo.sendUserEvent(UserEvent.PurchaseComplete(listOf(Product("purchase-completed-item-id", 1)))) {
                Log.d("HomeActivity", "PurchaseComplete user event sent")
            }
        }

        binding.addToCartButton.setOnClickListener {
            Gentoo.sendUserEvent(UserEvent.AddToCart(listOf(Product("cart-added-item-id", 1)))) {
                Log.d("HomeActivity", "AddToCart user event sent")
            }
        }
    }
}

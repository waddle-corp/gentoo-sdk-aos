package com.waddle.gentoo.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.waddle.gentoo.databinding.ViewGentooNavigationBarBinding

class GentooNavigationBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding: ViewGentooNavigationBarBinding = ViewGentooNavigationBarBinding.inflate(
        LayoutInflater.from(context)
    )

    init {
        addView(binding.root)
    }

    fun setBackButtonClickListener(listener: (() -> Unit)?) {
        if (listener == null) {
            binding.backArrow.setOnClickListener(null)
        } else {
            binding.backArrow.setOnClickListener { listener() }
        }
    }
}

package com.waddle.gentoo.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import com.waddle.gentoo.R
import com.waddle.gentoo.databinding.ViewGentooFloatingActionButtonBinding
import com.waddle.gentoo.internal.api.response.FloatingCommentResponse
import com.waddle.gentoo.internal.util.toDp

class GentooFloatingActionButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding: ViewGentooFloatingActionButtonBinding = ViewGentooFloatingActionButtonBinding.inflate(
        LayoutInflater.from(context)
    )

    var isExpanded: Boolean = false
        private set

    init {
        binding.gentooImageButton.setImageResource(R.drawable.icon_gentoo)
        binding.gentooDescription.text = "술 전문가 젠투에게 술 추천 받아보세요! 술 전문가 젠투에게 술 추천 받아보세요!"
        this.addView(binding.root)
        binding.root.setOnClickListener {
            if (isExpanded) shrink() else expand()
        }
    }

    fun setFloatingComment(floatingCommentResponse: FloatingCommentResponse) {
    }

    fun expand() {
        isExpanded = true
        binding.gentooDescription.visibility = VISIBLE
        binding.root.updateLayoutParams {
            width = ViewGroup.LayoutParams.WRAP_CONTENT // 300.toDp(context)
            height = 50.toDp(context)
        }
        binding.gentooImageButton.updateLayoutParams {
            width = 40.toDp(context)
            height = 40.toDp(context)
        }
        binding.gentooContainer.setPadding(24.toDp(context), 0, 5.toDp(context), 0)
    }

    fun shrink() {
        isExpanded = false
        binding.gentooDescription.visibility = GONE
        binding.root.updateLayoutParams {
            width = 55.toDp(context)
            height = 55.toDp(context)
        }
        binding.gentooImageButton.updateLayoutParams {
            width = 54.toDp(context)
            height = 54.toDp(context)
        }
        binding.gentooContainer.setPadding(0, 0, 0, 0)
    }
}

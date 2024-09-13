package com.waddle.gentoo.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import com.waddle.gentoo.Gentoo
import com.waddle.gentoo.R
import com.waddle.gentoo.databinding.ViewGentooFloatingActionButtonBinding
import com.waddle.gentoo.internal.util.toDp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GentooFloatingActionButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding: ViewGentooFloatingActionButtonBinding = ViewGentooFloatingActionButtonBinding.inflate(
        LayoutInflater.from(context)
    )

    var chatUrl: String? = null

    var isExpanded: Boolean = false
        private set

    init {
        binding.gentooImageButton.setImageResource(R.drawable.icon_gentoo)
        this.addView(binding.root)


        this.binding.root.setOnClickListener {
            chatUrl?.let {
                // TODO : go to chat webview here
            }
        }


        CoroutineScope(Dispatchers.IO).launch {
            try {
                val floatingComment = Gentoo.fetchFloatingComment("3190")
                withContext(Dispatchers.Main) {
                    binding.root.visibility = VISIBLE
                    binding.gentooDescription.text = floatingComment.commentForThis
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.root.visibility = GONE
                }
            }
        }

        this.postDelayed(
            { expand() },
            AUTO_EXPAND_DELAY
        )
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

    companion object {
        const val AUTO_EXPAND_DELAY = 3000L
    }
}

package com.waddle.gentoo.ui

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import com.waddle.gentoo.FloatingActionButtonType
import com.waddle.gentoo.R
import com.waddle.gentoo.databinding.ViewGentooFloatingActionButtonBinding
import com.waddle.gentoo.internal.util.toDp
import com.waddle.gentoo.viewmodel.GentooViewModel

class GentooFloatingActionButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding: ViewGentooFloatingActionButtonBinding = ViewGentooFloatingActionButtonBinding.inflate(
        LayoutInflater.from(context)
    )
    var onDismiss: (() -> Unit)? = null
    var onClick: (() -> Unit)? = null
    var chatUrl: String = ""
    var uiState: GentooViewModel.UiState = GentooViewModel.UiState.Invisible
        set(value) {
            field = value
            when (value) {
                is GentooViewModel.UiState.Collapsed -> {
                    binding.root.visibility = VISIBLE
                    shrink()
                }
                is GentooViewModel.UiState.Expanded -> {
                    binding.root.visibility = VISIBLE
                    updateComment(value.comment)
                    expand()
                }

                GentooViewModel.UiState.Invisible -> binding.root.visibility = GONE
            }
        }

    init {
        binding.gentooImageButton.setImageResource(R.drawable.icon_gentoo)
        this.addView(binding.root)

        this.binding.root.setOnClickListener {
            val url = this.chatUrl.takeIf { it.isNotEmpty() } ?: return@setOnClickListener
            val type = when (val state = uiState) {
                is GentooViewModel.UiState.Expanded -> state.type
                is GentooViewModel.UiState.Collapsed -> state.type
                else -> return@setOnClickListener
            }

            onClick?.invoke()
            when (type) {
                FloatingActionButtonType.HOME -> {
                    val intent = Intent(context, GentooChatActivity::class.java)
                    intent.putExtra(GentooChatActivity.INTENT_CHAT_URL, url)
                    context.startActivity(intent)
                }
                FloatingActionButtonType.DETAIL -> {
                    GentooBottomSheetDialog(context, url) { onDismiss?.invoke() }.show()
                }
            }
        }
    }

    private fun expand() {
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

    private fun shrink() {
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

    private fun updateComment(text: String) {
        binding.gentooDescription.text = text
    }
}

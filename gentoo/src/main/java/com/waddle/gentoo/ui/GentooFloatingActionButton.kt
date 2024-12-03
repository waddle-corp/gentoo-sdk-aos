package com.waddle.gentoo.ui

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.waddle.gentoo.DisplayLocation
import com.waddle.gentoo.Logger
import com.waddle.gentoo.databinding.ViewGentooFloatingActionButtonBinding
import com.waddle.gentoo.internal.util.Constants
import com.waddle.gentoo.internal.util.loadGif
import com.waddle.gentoo.viewmodel.GentooViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
    var onViewRendered: () -> Unit = {}
    var onGifAnimationEnded: () -> Unit = {}
    var onTextAnimationEnded: () -> Unit = {}
    var beenViewRendered: Boolean = false

    internal var chatUrl: String = ""
    var uiState: GentooViewModel.UiState = GentooViewModel.UiState.Invisible
        set(value) {
            Logger.d("uiState updated [$field] >> [$value]")

            if (!beenViewRendered && !field.isVisible && value.isVisible) {
                beenViewRendered = true
                onViewRendered()
            }

            field = value
            when (value) {
                is GentooViewModel.UiState.Collapsed -> {
                    binding.root.visibility = VISIBLE
                    binding.gentooDescription.visibility = GONE
                }
                is GentooViewModel.UiState.Expanded -> {
                    binding.root.visibility = VISIBLE
                    binding.gentooDescription.visibility = VISIBLE
                    updateComment(value.comment)
                }

                GentooViewModel.UiState.Invisible -> binding.root.visibility = GONE
                is GentooViewModel.UiState.Expanding -> {
                    startTextViewAnimation(value.comment)
                    binding.gentooDescription.visibility = VISIBLE
                }
                is GentooViewModel.UiState.GifAnimating -> {
                    binding.root.visibility = VISIBLE
                    binding.gentooDescription.visibility = GONE
                    startGifAnimation()
                }
            }
        }

    init {
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        this.addView(binding.root)

        this.binding.root.setOnClickListener {
            val url = this.chatUrl.takeIf { it.isNotEmpty() } ?: return@setOnClickListener
            val type = when (val state = uiState) {
                is GentooViewModel.UiState.GifAnimating -> state.displayLocation
                is GentooViewModel.UiState.Expanding -> state.displayLocation
                is GentooViewModel.UiState.Expanded -> state.displayLocation
                is GentooViewModel.UiState.Collapsed ->state.displayLocation
                else -> return@setOnClickListener
            }

            onClick?.invoke()
            when (type) {
                DisplayLocation.HOME -> {
                    val intent = Intent(context, GentooChatActivity::class.java)
                    intent.putExtra(GentooChatActivity.INTENT_CHAT_URL, url)
                    context.startActivity(intent)
                }
                DisplayLocation.PRODUCT_DETAIL -> {
                    GentooBottomSheetDialog(context, url) { onDismiss?.invoke() }.show()
                }

                DisplayLocation.PRODUCT_LIST -> {} // TODO
            }
        }
    }

    private fun startGifAnimation(
        url: String = Constants.FAB_IMAGE_URL
    ) {
        binding.gentooImageButton.loadGif(url) {
            onGifAnimationEnded()
        }
    }

    private fun startTextViewAnimation(text: String) {
        binding.gentooDescription.text = ""
        binding.gentooDescription.visibility = VISIBLE
        CoroutineScope(Dispatchers.Main).launch {
            for (i in text.indices) {
                delay(90)
                binding.gentooDescription.text = text.substring(0, i + 1)
            }
            onTextAnimationEnded()
        }
    }

    private fun updateComment(text: String) {
        binding.gentooDescription.text = text
    }
}

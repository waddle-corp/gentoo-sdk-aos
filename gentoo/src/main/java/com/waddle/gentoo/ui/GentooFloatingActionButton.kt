package com.waddle.gentoo.ui

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.waddle.gentoo.ChatType
import com.waddle.gentoo.FloatingActionButtonType
import com.waddle.gentoo.Gentoo
import com.waddle.gentoo.R
import com.waddle.gentoo.databinding.ViewGentooFloatingActionButtonBinding
import com.waddle.gentoo.internal.util.toDp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
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

    private var chatUrl: String? = null
    private var prepareJob: Job? = null

    var isExpanded: Boolean = false
        private set

    var itemId: String? = null
        set(value) {
            field = value
            prepare()
        }

    var floatingActionButtonType: FloatingActionButtonType = FloatingActionButtonType.HOME

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.GentooFloatingActionButton, defStyleAttr, 0)
            floatingActionButtonType = when (typedArray.getInt(R.styleable.GentooFloatingActionButton_chat_type, 0)) {
                0 -> FloatingActionButtonType.HOME
                1 -> FloatingActionButtonType.DETAIL
                else -> FloatingActionButtonType.HOME // default
            }

            typedArray.recycle()
        }

        binding.gentooImageButton.setImageResource(R.drawable.icon_gentoo)
        this.addView(binding.root)

        this.binding.root.setOnClickListener {
            chatUrl?.let {
                val intent = Intent(context, GentooChatActivity::class.java)
                intent.putExtra(GentooChatActivity.INTENT_CHAT_URL, it)
                context.startActivity(intent)
            }
        }

        prepare()
    }

    private fun prepare() {
        prepareJob?.cancel()
        prepareJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                when (floatingActionButtonType) {
                    FloatingActionButtonType.HOME -> {
                        coroutineScope {
                            this@GentooFloatingActionButton.chatUrl = Gentoo.getHomeChatUrl()
                        }
                        updateComment(HOME_FAB_COMMENT)
                        expand()
                        delay(AUTO_EXPAND_DELAY)
                        shrink()
                    }
                    FloatingActionButtonType.DETAIL -> {
                        val itemId = this@GentooFloatingActionButton.itemId ?: return@launch
                        val floatingComment = Gentoo.fetchFloatingComment(itemId)
                        updateComment(floatingComment.commentForThis)
                        this@GentooFloatingActionButton.chatUrl = Gentoo.getDetailChatUrl(itemId, ChatType.THIS, floatingComment.commentForThis)
                        // TODO shrink and expand
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.root.visibility = GONE
                }
            }
        }
    }

    private suspend fun expand() = withContext(Dispatchers.Main) {
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

    private suspend fun shrink() = withContext(Dispatchers.Main) {
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

    private suspend fun updateComment(text: String) = withContext(Dispatchers.Main) {
        binding.gentooDescription.text = text
    }

    companion object {
        private const val AUTO_EXPAND_DELAY = 3000L
        private const val HOME_FAB_COMMENT = "술 전문가 젠투에게 술 추천 받아보세요!"
    }
}

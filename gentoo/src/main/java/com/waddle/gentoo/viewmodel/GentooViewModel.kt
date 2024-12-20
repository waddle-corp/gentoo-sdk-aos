package com.waddle.gentoo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waddle.gentoo.CommentType
import com.waddle.gentoo.DisplayLocation
import com.waddle.gentoo.Gentoo
import com.waddle.gentoo.Logger
import com.waddle.gentoo.internal.api.request.FloatingCommentData
import com.waddle.gentoo.internal.api.request.UserEvent
import com.waddle.gentoo.internal.api.response.FloatingComment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class GentooViewModel(
    private val displayLocation: DisplayLocation,
    private val floatingCommentData: FloatingCommentData
) : ViewModel() {
    abstract val itemId: String?

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Invisible)
    val uiState: StateFlow<UiState> = _uiState

    private val _chatUrl: MutableStateFlow<String> = MutableStateFlow("")
    val chatUrl: StateFlow<String> = _chatUrl

    private var isGifAnimationEnded: Boolean = false
    private var isTextAnimationEnded: Boolean = false

    private suspend fun changeUiState(uiState: UiState) {
        Logger.i("${this::class.simpleName}.changeUiState() ${_uiState.value} >> $uiState ")
        _uiState.emit(uiState)
    }

    internal abstract suspend fun getChatUrl(floatingComment: FloatingComment): String

    open fun onBottomSheetDismissed() {}

    open fun onClicked() {}

    fun showFloatingButtonComment() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val floatingComment = Gentoo.fetchFloatingComment(floatingCommentData)
                changeUiState(UiState.GifAnimating(displayLocation, floatingComment.imageUrl))
                Logger.d("GentooViewModel($displayLocation).showFloatingButtonComment() >> fetchFloatingComment result: $floatingComment")
                val chatUrl = getChatUrl(floatingComment)
                _chatUrl.emit(chatUrl)
                Logger.d("GentooViewModel($displayLocation).showFloatingButtonComment() >> chat url : $chatUrl")
                waitForGifAnimation()
                changeUiState(UiState.Expanding(displayLocation, floatingComment.comment))
                waitForTextAnimation()
                changeUiState(UiState.Expanded(displayLocation, floatingComment.comment))
                delay(AUTO_COLLAPSED_DELAY)
                changeUiState(UiState.Collapsed(displayLocation))
            } catch (e: Exception) {
                Logger.w("GentooViewModel($displayLocation).showFloatingButtonComment() e: $e")
            }
        }
    }

    fun onGifAnimationEnded() {
        isGifAnimationEnded = true
    }

    fun onTextAnimationEnded() {
        isTextAnimationEnded = true
    }

    fun markAsFloatingButtonRendered() {
        CoroutineScope(Dispatchers.IO).launch {
            Gentoo.sendUserEvent(UserEvent.SdkFloatingRendered)
        }
    }

    fun markAsFloatingButtonClicked() {
        CoroutineScope(Dispatchers.IO).launch {
            Gentoo.sendUserEvent(UserEvent.SdkFloatingClicked)
        }
    }

    private suspend fun waitForGifAnimation() {
        while (!isGifAnimationEnded) {
            delay(20)
        }
    }

    private suspend fun waitForTextAnimation() {
        while (!isTextAnimationEnded) {
            delay(20)
        }
    }

    sealed class UiState {
        internal abstract val isVisible: Boolean
        data object Invisible : UiState() {
            override val isVisible: Boolean = false
        }

        data class GifAnimating(val displayLocation: DisplayLocation, val imageUrl: String) : UiState() {
            override val isVisible: Boolean = true
        }

        data class Expanding(val displayLocation: DisplayLocation, val comment: String) : UiState() {
            override val isVisible: Boolean = true
        }

        data class Expanded(val displayLocation: DisplayLocation, val comment: String) : UiState() {
            override val isVisible: Boolean = true
        }

        data class Collapsed(val displayLocation: DisplayLocation) : UiState() {
            override val isVisible: Boolean = false
        }
    }

    companion object {
        internal const val AUTO_COLLAPSED_DELAY = 3000L
    }
}

class GentooHomeViewModel : GentooViewModel(
    DisplayLocation.HOME,
    FloatingCommentData.Home
) {
    override val itemId: String?
        get() = null

    override suspend fun getChatUrl(floatingComment: FloatingComment): String {
        return Gentoo.getDefaultChatUrl()
    }
}


class GentooProductListViewModel : GentooViewModel(
    DisplayLocation.PRODUCT_LIST,
    FloatingCommentData.ProductList
) {
    override val itemId: String?
        get() = null

    override suspend fun getChatUrl(floatingComment: FloatingComment): String {
        return Gentoo.getDefaultChatUrl()
    }
}

class GentooProductDetailViewModel(
    override val itemId: String?
) : GentooViewModel(
    DisplayLocation.PRODUCT_DETAIL,
    FloatingCommentData.ProductDetail(itemId, CommentType.THIS)
) {
    override suspend fun getChatUrl(floatingComment: FloatingComment): String {
        return Gentoo.getDefaultChatUrl()
    }
}

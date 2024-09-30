package com.waddle.gentoo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waddle.gentoo.ChatType
import com.waddle.gentoo.FloatingActionButtonType
import com.waddle.gentoo.Gentoo
import com.waddle.gentoo.internal.api.response.FloatingComment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class GentooViewModel : ViewModel() {
    protected val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Invisible)
    val uiState: StateFlow<UiState> = _uiState

    protected val _chatUrl: MutableStateFlow<String> = MutableStateFlow("")
    val chatUrl: StateFlow<String> = _chatUrl

    open fun onBottomSheetDismissed() {}

    open fun onClicked() {}

    sealed class UiState {
        data object Invisible : UiState()
        data class Expanded(val type: FloatingActionButtonType, val comment: String) : UiState()
        data class Collapsed(val type: FloatingActionButtonType) : UiState()
    }

    companion object {
        internal const val AUTO_COLLAPSED_DELAY = 3000L
        internal const val COMMENT_CHANGE_TO_NEEDS_DELAY = 10000L
    }
}

class GentooDefaultViewModel : GentooViewModel() {
    var uiStateJob: Job? = null
    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val floatingComment = Gentoo.fetchFloatingComment(ChatType.DEFAULT, "default")
                _uiState.emit(UiState.Expanded(FloatingActionButtonType.DEFAULT, floatingComment.message))
                _chatUrl.emit(Gentoo.getDefaultChatUrl())
                delay(AUTO_COLLAPSED_DELAY)
                _uiState.emit(UiState.Collapsed(FloatingActionButtonType.DEFAULT))
            } catch (_: Exception) {}
        }
    }
}

class GentooDetailViewModel(
    val itemId: String
) : GentooViewModel() {
    private var uiStateJob: Job? = null
    private var currentChatType: ChatType = ChatType.DEFAULT
    private var thisFloatingComment: FloatingComment? = null
    private var needsFloatingComment: FloatingComment? = null
    init {
        updateFloatingComment(false)
    }

    private fun updateFloatingComment(fromNeeds: Boolean) {
        uiStateJob = viewModelScope.launch(Dispatchers.IO) {
            if (!fromNeeds) {
                currentChatType = ChatType.THIS
                val thisFloatingComment = thisFloatingComment ?: Gentoo.fetchFloatingComment(ChatType.THIS, itemId).also {
                    this@GentooDetailViewModel.thisFloatingComment = it
                }

                _chatUrl.emit(Gentoo.getDetailChatUrl(itemId, ChatType.THIS, thisFloatingComment.message))
                _uiState.emit(UiState.Expanded(FloatingActionButtonType.DETAIL, thisFloatingComment.message))
                delay(AUTO_COLLAPSED_DELAY)
            }

            _uiState.emit(UiState.Collapsed(FloatingActionButtonType.DETAIL))
            val deferred = async {
                this@GentooDetailViewModel.needsFloatingComment ?: Gentoo.fetchFloatingComment(ChatType.NEEDS, itemId).also {
                    this@GentooDetailViewModel.needsFloatingComment = it
                }
            }
            delay(COMMENT_CHANGE_TO_NEEDS_DELAY)
            currentChatType = ChatType.NEEDS
            val needsFloatingComment = deferred.await()
            _chatUrl.emit(Gentoo.getDetailChatUrl(itemId, ChatType.NEEDS,  needsFloatingComment.message))
            _uiState.emit(UiState.Expanded(FloatingActionButtonType.DETAIL, needsFloatingComment.message))
            delay(AUTO_COLLAPSED_DELAY)
            _uiState.emit(UiState.Collapsed(FloatingActionButtonType.DETAIL))
        }
    }

    override fun onClicked() {
        uiStateJob?.cancel()
    }

    override fun onBottomSheetDismissed() {
        uiStateJob?.cancel()
        if (currentChatType == ChatType.THIS) {
            updateFloatingComment(true)
        } else {
            viewModelScope.launch {
                _uiState.emit(UiState.Collapsed(FloatingActionButtonType.DETAIL))
            }
        }
    }
}

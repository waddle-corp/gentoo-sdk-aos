package com.waddle.gentoo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waddle.gentoo.ChatType
import com.waddle.gentoo.FloatingActionButtonType
import com.waddle.gentoo.Gentoo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class GentooViewModel : ViewModel() {
    protected val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Invisible)
    val uiState: StateFlow<UiState> = _uiState

    protected val _chatUrl: MutableStateFlow<String> = MutableStateFlow("")
    val chatUrl: StateFlow<String> = _chatUrl

    sealed class UiState {
        data object Invisible : UiState()
        data class Expanded(val type: FloatingActionButtonType, val comment: String) : UiState()
        data class Collapsed(val type: FloatingActionButtonType) : UiState()
    }

    companion object {
        internal const val AUTO_COLLAPSED_DELAY = 3000L
        internal const val COMMENT_CHANGE_TO_NEEDS_DELAY = 10000L
        internal const val HOME_FAB_COMMENT = "술 전문가 젠투에게 술 추천 받아보세요!"
    }
}

class GentooHomeViewModel : GentooViewModel() {
    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.emit(UiState.Expanded(FloatingActionButtonType.HOME, HOME_FAB_COMMENT))
                _chatUrl.emit(Gentoo.getHomeChatUrl())
                delay(AUTO_COLLAPSED_DELAY)
                _uiState.emit(UiState.Collapsed(FloatingActionButtonType.HOME))
            } catch (_: Exception) {}
        }
    }
}

class GentooDetailViewModel(
    val itemId: String
) : GentooViewModel() {
    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val floatingComment = Gentoo.fetchFloatingComment(itemId)
                _chatUrl.emit(Gentoo.getDetailChatUrl(itemId, ChatType.THIS, floatingComment.commentForThis))
                _uiState.emit(UiState.Expanded(FloatingActionButtonType.DETAIL, floatingComment.commentForThis))
                delay(AUTO_COLLAPSED_DELAY)
                _uiState.emit(UiState.Collapsed(FloatingActionButtonType.DETAIL))
                delay(COMMENT_CHANGE_TO_NEEDS_DELAY)
                _chatUrl.emit(Gentoo.getDetailChatUrl(itemId, ChatType.NEEDS, floatingComment.commentForNeeds))
                _uiState.emit(UiState.Expanded(FloatingActionButtonType.DETAIL, floatingComment.commentForNeeds))
                delay(AUTO_COLLAPSED_DELAY)
                _uiState.emit(UiState.Collapsed(FloatingActionButtonType.DETAIL))
            } catch (_: Exception) {}
        }
    }
}

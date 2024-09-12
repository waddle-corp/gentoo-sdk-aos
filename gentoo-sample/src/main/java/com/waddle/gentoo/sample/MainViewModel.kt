package com.waddle.gentoo.sample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waddle.gentoo.Gentoo
import com.waddle.gentoo.internal.api.response.FloatingComment
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _floatingComment: MutableLiveData<FloatingComment?> = MutableLiveData(null)
    val floatingComment: LiveData<FloatingComment?>
        get() = _floatingComment

    private val _chatUrl: MutableLiveData<String?> = MutableLiveData(null)
    val chatUrl: LiveData<String?>
        get() = _chatUrl

    init {
    }

    fun fetchFloatingComment() {
        // TODO : should wait until authenticate finishes
        viewModelScope.launch {
            try {
                Gentoo.authenticate("test", "test")
                val floatingComment = Gentoo.fetchFloatingComment("3190", Gentoo.authResponse?.body?.randomId!!) // TODO : remove `!!`
                _floatingComment.postValue(floatingComment)
            } catch (_: Exception) {}
        }
    }

    fun fetchChatUrl() {
        viewModelScope.launch {
            try {
                Gentoo.authenticate("test", "test")
                val chatUrl = Gentoo.getChatUrl("test", "3190", "dlst", "3190")
                _chatUrl.postValue(chatUrl)
            } catch (_: Exception) {}
        }
    }
}

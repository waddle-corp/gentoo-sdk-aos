package com.waddle.gentoo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class GentooDetailViewModelFactory(private val itemId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GentooDetailViewModel::class.java)) {
            return GentooDetailViewModel(itemId) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

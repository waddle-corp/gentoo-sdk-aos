package com.waddle.gentoo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class GentooProductDetailViewModelFactory(private val itemId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GentooProductDetailViewModel::class.java)) {
            return GentooProductDetailViewModel(itemId) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

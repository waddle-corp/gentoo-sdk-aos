package com.waddle.gentoo.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.waddle.gentoo.databinding.DialogGentooBottomSheetBinding
import com.waddle.gentoo.internal.util.toDp

@SuppressLint("ClickableViewAccessibility")
class GentooBottomSheetDialog(
    context: Context,
) : BottomSheetDialog(context) {
    private val binding: DialogGentooBottomSheetBinding = DialogGentooBottomSheetBinding.inflate(
        LayoutInflater.from(context)
    )

    init {
        this.setContentView(binding.root)

        val bottomSheet = this.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        if (bottomSheet != null) {
            val behavior = BottomSheetBehavior.from(bottomSheet)

            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            behavior.peekHeight = 535.toDp(context)
            behavior.isFitToContents = true
            behavior.skipCollapsed = false
            bottomSheet.layoutParams.height = LayoutParams.MATCH_PARENT
        }

        binding.closeButton.setOnClickListener {
            this.dismiss()
        }
    }
}

package com.waddle.gentoo.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams
import androidx.core.view.updateLayoutParams
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.waddle.gentoo.databinding.DialogGentooBottomSheetBinding
import com.waddle.gentoo.internal.util.toDp

@SuppressLint("ClickableViewAccessibility")
class GentooBottomSheetDialog(
    context: Context,
    private val chatUrl: String,
) : BottomSheetDialog(context) {
    private val binding: DialogGentooBottomSheetBinding = DialogGentooBottomSheetBinding.inflate(
        LayoutInflater.from(context)
    )

    private val collapsedHeight = 535.toDp(context)

    init {
        this.setContentView(binding.root)

        val bottomSheet = this.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        if (bottomSheet != null) {
            val behavior = BottomSheetBehavior.from(bottomSheet)

            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            behavior.peekHeight = collapsedHeight
            behavior.isFitToContents = true
            behavior.skipCollapsed = false
            bottomSheet.layoutParams.height = LayoutParams.MATCH_PARENT
            binding.root.updateLayoutParams { height = collapsedHeight }

            behavior.addBottomSheetCallback(
                object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                            binding.root.updateLayoutParams { height = LayoutParams.MATCH_PARENT }
                        } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                            binding.root.updateLayoutParams { height = collapsedHeight }
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                }
            )

            binding.gentooChatWebview.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    behavior.isDraggable = false
                } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                    behavior.isDraggable = true
                }

                return@setOnTouchListener false
            }
        }

        binding.closeButton.setOnClickListener {
            this.dismiss()
        }

        binding.gentooChatWebview.loadUrl(chatUrl)
    }
}

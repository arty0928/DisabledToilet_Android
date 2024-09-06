package com.example.disabledtoilet_android

import android.content.Context
import android.content.Intent
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

object BottomSheetHelper {
    fun showBottomSheetDialog(
        context: Context,
        layoutInflater: LayoutInflater,
        displayMetrics: DisplayMetrics
    ) {
        val bottomSheetDialog = BottomSheetDialog(context)

        // Inflate detail_bottomsheet layout as the initial bottom sheet's content
        val bottomSheetView = layoutInflater.inflate(R.layout.detail_bottomsheet, null)
        bottomSheetDialog.setContentView(bottomSheetView)

        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheetDialog = dialog as? BottomSheetDialog
            val bottomSheet = bottomSheetDialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheet?.let {
                // Set the background of the bottom sheet to transparent
                it.setBackgroundResource(android.R.color.transparent)

                // Set the initial height of the bottom sheet to 250dp
                val initialHeightInPx = (250 * displayMetrics.density).toInt()
                val behavior = BottomSheetBehavior.from(it)

                behavior.peekHeight = initialHeightInPx
                it.layoutParams.height = initialHeightInPx
                it.requestLayout()
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED

                // Set the BottomSheetCallback to handle sliding
                behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        // Handle state changes if necessary
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        // Handle sliding if necessary
                    }
                })
            }

            // Remove background dimming
            if (bottomSheetDialog != null) {
                bottomSheetDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            }
        }

        bottomSheetDialog.window?.setBackgroundDrawable(null)

        // Set an OnClickListener for the R.id.more_button to transition to DetailPageActivity
        bottomSheetView.findViewById<View>(R.id.more_button)?.setOnClickListener {
            // Close the BottomSheetDialog
            bottomSheetDialog.dismiss()

            // Start DetailActivity without animation
            val intent = Intent(context, DetailActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            context.startActivity(intent)
        }

        bottomSheetDialog.show()
    }
}

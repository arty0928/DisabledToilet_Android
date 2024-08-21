package com.example.disabledtoilet_android

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class NearActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_near)

        // Find the ImageButton by its ID
        val backButton: ImageButton = findViewById(R.id.back_button)

        // Set an OnClickListener to call onBackPressed() when the button is clicked
        backButton.setOnClickListener {
            onBackPressed()
        }

        val mapButton: ImageButton = findViewById(R.id.map_icon)

        // Set an OnClickListener to call onBackPressed() when the button is clicked
        mapButton.setOnClickListener {
            showBottomSheetDialog()
        }
    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)

        val bottomSheetView = layoutInflater.inflate(R.layout.detail_bottomsheet, null)

        // Set the background of the dialog to be transparent
        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheetDialogInstance = dialog as BottomSheetDialog
            val bottomSheet = bottomSheetDialogInstance.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheet?.let {
                // Set the background of the bottom sheet to transparent
                it.setBackgroundResource(android.R.color.transparent)

                // Set the height of the bottom sheet to 250dp
                val behavior = BottomSheetBehavior.from(it)
                val displayMetrics = resources.displayMetrics
                val heightInPx = (250 * displayMetrics.density).toInt() // Convert 250dp to pixels
                behavior.peekHeight = heightInPx
                it.layoutParams.height = heightInPx
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
            
            //배경 어두워지는 거 제거
            bottomSheetDialogInstance.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        bottomSheetDialog.show()
    }


}

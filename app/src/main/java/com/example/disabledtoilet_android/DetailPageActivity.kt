package com.example.disabledtoilet_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class DetailPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // 다이얼로그 표시
        val expandedDetailBottomSheet = ExpandedDetailBottomSheet()
        expandedDetailBottomSheet.show(supportFragmentManager, "ExpandedDetailBottomSheet")
    }
}

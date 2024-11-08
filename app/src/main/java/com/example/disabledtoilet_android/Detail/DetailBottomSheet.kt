package com.example.disabledtoilet_android.Detail

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.disabledtoilet_android.R

class DetailBottomSheet : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_bottomsheet)

        // 더보기 버튼
        val moreButton: TextView = findViewById(R.id.more_button)
        moreButton.setOnClickListener {
            val intent = Intent(this, DetailPageActivity::class.java)
            startActivity(intent)
        }

        // save 버튼
        val saveButton1: LinearLayout = findViewById(R.id.save_btn1)
        val saveButton2: LinearLayout = findViewById(R.id.save_btn2)

        // ImageView 참조
        val save_icon1: ImageView = findViewById(R.id.save_icon1)
        val save_icon2: ImageView = findViewById(R.id.save_icon2)

        // 현재 상태를 추적하기 위한 변수
        var isLogoDisplayed = false

        // 클릭 리스너 설정
        val toggleImageSrc = {
            if (isLogoDisplayed) {
                save_icon1.setImageResource(R.drawable.save_icon)
                save_icon2.setImageResource(R.drawable.save_icon)

            } else {
                save_icon1.setImageResource(R.drawable.saved_star_icon)
                save_icon2.setImageResource(R.drawable.saved_star_icon)
            }
            isLogoDisplayed = !isLogoDisplayed
        }

        saveButton1.setOnClickListener { toggleImageSrc() }
        saveButton2.setOnClickListener { toggleImageSrc() }
    }
}

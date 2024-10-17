package com.example.disabledtoilet_android

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailBottomSheet : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_bottomsheet)

        //더보기 버튼
        val moreButton : TextView = findViewById(R.id.more_button)

        moreButton.setOnClickListener{
            val intent = Intent(this, DetailActivity::class.java)
            startActivity(intent)
        }

    }

}

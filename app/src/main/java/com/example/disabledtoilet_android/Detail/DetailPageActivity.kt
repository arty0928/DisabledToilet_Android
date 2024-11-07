package com.example.disabledtoilet_android.Detail

import ToiletModel
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.disabledtoilet_android.R
import com.example.disabledtoilet_android.databinding.ActivityDetailBinding

class DetailPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로 가기 버튼 설정
        val backButton = binding.backButton
        backButton.setOnClickListener {
            onBackPressed()
        }

        // 전달받은 화장실 데이터
        val toiletData = intent.getParcelableExtra<ToiletModel>("TOILET_DATA")


        // DetailOptionFragment를 fragment_container에 추가
        if (savedInstanceState == null) {
            val fragment = DetailOptionFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("TOILET_DATA", toiletData)
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }
}

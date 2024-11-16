package com.example.disabledtoilet_android.User

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.disabledtoilet_android.R
import com.example.disabledtoilet_android.databinding.ActivityMypageBinding
import com.example.disabledtoilet_android.User.Model.Recent_viewed_toilet

class MyPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMypageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 데이터 바인딩 객체 초기화
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로 가기 버튼 클릭 리스너 설정
        binding.backButton.setOnClickListener {
            onBackPressed() // 이전 화면으로 돌아가기
        }

        // 최근 본 화장실 데이터 목록 생성
        val toiletList = listOf(
            Recent_viewed_toilet("화장실 1", "https://cdn.travie.com/news/photo/first/201710/img_19975_1.jpg"),
            Recent_viewed_toilet("화장실 2", "https://cdn.travie.com/news/photo/first/201710/img_19975_1.jpg"),
            Recent_viewed_toilet("화장실 3", "https://cdn.travie.com/news/photo/first/201710/img_19975_1.jpg"),
            Recent_viewed_toilet("화장실 1", "https://cdn.travie.com/news/photo/first/201710/img_19975_1.jpg"),
            Recent_viewed_toilet("화장실 2", "https://cdn.travie.com/news/photo/first/201710/img_19975_1.jpg"),
            Recent_viewed_toilet("화장실 3", "https://cdn.travie.com/news/photo/first/201710/img_19975_1.jpg")
        )

        // 최근 본 화장실 뷰 추가
        addToiletViews(toiletList, binding.recentViewedSectionLinear)

    }

    // 화장실 목록을 받아서 해당 LinearLayout에 뷰를 추가하는 메소드
    private fun addToiletViews(toiletList: List<Recent_viewed_toilet>, parentLinear: LinearLayout) {
        for (toilet in toiletList) {
            // 아이템 뷰를 인플레이트하여 추가
            val itemView = layoutInflater.inflate(R.layout.item_recent_viewed_toilet, parentLinear, false)

            // 아이템 뷰의 이미지와 텍스트 뷰 찾기
            val imageView = itemView.findViewById<ImageView>(R.id.toilet_img)
            val textView = itemView.findViewById<TextView>(R.id.toilet_name)

            // 화장실 이름 설정
            textView.text = toilet.name

            // Glide를 사용하여 이미지 로드
            Glide.with(this)
                .load(toilet.imageUrl)
                .into(imageView)

            // 부모 LinearLayout에 아이템 추가
            parentLinear.addView(itemView)
        }
    }
}

package com.example.disabledtoilet_android.User

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.disabledtoilet_android.databinding.ActivityMypageBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.disabledtoilet_android.R
import com.example.disabledtoilet_android.User.Model.Recent_viewed_toilet

class MyPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMypageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로 가기 버튼 설정
        val backButton = binding.backButton
        backButton.setOnClickListener {
            onBackPressed()
        }

        // 최근 본 화장실 데이터를 직접 HorizontalScrollView에 추가
        val toiletList = listOf(
            Recent_viewed_toilet("화장실 1", "https://cdn.travie.com/news/photo/first/201710/img_19975_1.jpg"),
            Recent_viewed_toilet("화장실 2", "https://cdn.travie.com/news/photo/first/201710/img_19975_1.jpg"),
            Recent_viewed_toilet("화장실 3", "https://cdn.travie.com/news/photo/first/201710/img_19975_1.jpg"),
            Recent_viewed_toilet("화장실 1", "https://cdn.travie.com/news/photo/first/201710/img_19975_1.jpg"),
            Recent_viewed_toilet("화장실 2", "https://cdn.travie.com/news/photo/first/201710/img_19975_1.jpg"),
            Recent_viewed_toilet("화장실 3", "https://cdn.travie.com/news/photo/first/201710/img_19975_1.jpg")
        )

        val recentViewLinear = binding.recentViewedSectionLinear

        // 데이터를 기반으로 뷰 추가
        for (toilet in toiletList) {
            // 아이템 뷰를 인플레이트하여 추가
            val itemView = layoutInflater.inflate(R.layout.item_recent_viewed_toilet, recentViewLinear, false)

            val imageView = itemView.findViewById<ImageView>(R.id.toilet_img)
            val textView = itemView.findViewById<TextView>(R.id.toilet_name)

            // 이름 설정
            textView.text = toilet.name

            // 이미지 설정 (Glide 사용)
            Glide.with(this)
                .load(toilet.imageUrl)
                .into(imageView)

            // LinearLayout에 아이템 추가
            recentViewLinear.addView(itemView)
        }

        //신고한 화장실
        val reportedtoiletList = listOf(
            Recent_viewed_toilet("화장실 1", "https://cdn.travie.com/news/photo/first/201710/img_19975_1.jpg"),
            Recent_viewed_toilet("화장실 2", "https://cdn.travie.com/news/photo/first/201710/img_19975_1.jpg"),
        )

        val reportedToiletLinear = binding.reportedToiletSectionLinear

        // 데이터를 기반으로 뷰 추가
        for (toilet in reportedtoiletList) {
            // 아이템 뷰를 인플레이트하여 추가
            val itemView = layoutInflater.inflate(R.layout.item_recent_viewed_toilet, reportedToiletLinear, false)

            val imageView = itemView.findViewById<ImageView>(R.id.toilet_img)
            val textView = itemView.findViewById<TextView>(R.id.toilet_name)

            // 이름 설정
            textView.text = toilet.name

            // 이미지 설정 (Glide 사용)
            Glide.with(this)
                .load(toilet.imageUrl)
                .into(imageView)

            // LinearLayout에 아이템 추가
            reportedToiletLinear.addView(itemView)
        }

    }
}

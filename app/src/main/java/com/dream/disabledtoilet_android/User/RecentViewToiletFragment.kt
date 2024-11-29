//package com.example.disabledtoilet_android.User
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.LinearLayout
//import android.widget.TextView
//import androidx.fragment.app.Fragment
//import com.bumptech.glide.Glide
//import com.example.disabledtoilet_android.R
//import com.example.disabledtoilet_android.User.Model.Recent_viewed_toilet
//
//class RecentViewedToiletFragment : Fragment() {
//
//    private lateinit var recentViewLinear: LinearLayout
//    private lateinit var toiletList: List<Recent_viewed_toilet>
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        // 예시 데이터 리스트
//        toiletList = listOf(
//            Recent_viewed_toilet("화장실 1", "https://cdn.travie.com/news/photo/first/201710/img_19975_1.jpg"),
//            Recent_viewed_toilet("화장실 2", "https://cdn.travie.com/news/photo/first/201710/img_19975_1.jpg"),
//            Recent_viewed_toilet("화장실 3", "https://cdn.travie.com/news/photo/first/201710/img_19975_1.jpg")
//        )
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Fragment의 레이아웃을 인플레이트합니다.
//        val view = inflater.inflate(R.layout.fragment_recent_view_toilet, container, false)
//        recentViewLinear = view.findViewById(R.id.recent_viewed_section_linear)
//
//        // 데이터를 기반으로 뷰 추가
//        for (toilet in toiletList) {
//            // 아이템 뷰를 인플레이트하여 추가
//            val itemView = inflater.inflate(R.layout.item_recent_viewed_toilet, recentViewLinear, false)
//
//            val imageView = itemView.findViewById<ImageView>(R.id.recent_view_toilet_img)
//            val textView = itemView.findViewById<TextView>(R.id.recent_viewed_toilet_name)
//
//            // 이름 설정
//            textView.text = toilet.name
//
//            // 이미지 설정 (Glide 사용)
//            Glide.with(this)
//                .load(toilet.imageUrl)
//                .into(imageView)
//
//            // LinearLayout에 아이템 추가
//            recentViewLinear.addView(itemView)
//        }
//
//        return view
//    }
//}

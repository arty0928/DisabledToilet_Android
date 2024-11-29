package com.dream.disabledtoilet_android.User

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dream.disabledtoilet_android.R
import com.dream.disabledtoilet_android.ToiletSearch.ToiletData
import com.dream.disabledtoilet_android.databinding.ActivityMypageBinding
import com.dream.disabledtoilet_android.User.Model.Recent_viewed_toilet
import com.dream.disabledtoilet_android.User.ViewModel.UserViweModel

class MyPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMypageBinding

    lateinit var userViewModel : UserViweModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 데이터 바인딩 객체 초기화
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this)[UserViweModel::class.java]

        // 뒤로 가기 버튼 클릭 리스너 설정
        binding.backButton.setOnClickListener {
            onBackPressed() // 이전 화면으로 돌아가기
        }

        //좋아요 화장실 갯수
        binding.mypageSaveCountTxt.text = ToiletData.currentUser?.recentlyViewedToilets?.size.toString()

        //등록한 화장실 갯수
        binding.mypageRegisterCountTxt.text = ToiletData.currentUser?.registedToilets?.size.toString()

        //사용자 이름
        binding.mypageUsernameTxt.text = ToiletData.currentUser!!.name!!.split("@")[0]


        // 최근 본 화장실 데이터 목록 생성
        val recentViewedToilet = ToiletData.currentUser!!.recentlyViewedToilets
        val toiletList = mutableListOf<Recent_viewed_toilet>()

        for(toilet in recentViewedToilet){
            toiletList.add(
                Recent_viewed_toilet(
                    name = toilet.restroom_name,
                    imageUrl = "toilet.restroom_name, \"https://cdn.travie.com/news/photo/first/201710/img_19975_1.jpg\""
                )
            )
        }
        addToiletViews(toiletList, binding.recentViewedSectionLinear)

        //찜한 화장실 뷰 추가
        val likedToilets = ToiletData.currentUser!!.likedToilets
        val likedToiletList = mutableListOf<Recent_viewed_toilet>()

        for(toilet in likedToilets){
            likedToiletList.add(
                Recent_viewed_toilet(
                    name = toilet.restroom_name,
                    imageUrl = "toilet.restroom_name, \"https://cdn.travie.com/news/photo/first/201710/img_19975_1.jpg\""
                )
            )
        }
        addToiletViews(likedToiletList, binding.recentViewedSectionLinear)

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

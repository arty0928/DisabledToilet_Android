package com.example.disabledtoilet_android.Detail

import ToiletModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.disabledtoilet_android.R

class FragmentFilterOption : Fragment() {

    private lateinit var optionTextView: TextView
    private lateinit var scrollViewContainer: LinearLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_filter_option, container, false)

        scrollViewContainer = view.findViewById(R.id.filterOptionContainer)

        // 전달받은 화장실 데이터
        val toiletData = arguments?.getParcelable<ToiletModel>("TOILET_DATA")

        // 원하는 필드에 접근하여 텍스트 설정
        // 조건에 따라 필드 추가
        toiletData?.let { toilet ->
            if (toilet.male_disabled_toilet_count > 0) {
                addTextView("남성 장애인용 화장실")
            }

            if (toilet.male_disabled_urinal_count > 0) {
                addTextView("남성 장애인용 소변기")
            }

            if (toilet.female_disabled_toilet_count > 0) {
                addTextView("여성 장애인용 화장실")
            }

            if (toilet.emergency_bell_location == "Y") {
                addTextView("비상벨")
            }

            if (toilet.restroom_entrance_cctv_installed == "Y") {
                addTextView("입구 CCTV 설치")
            }
        }

        return view
    }

    private fun addTextView(text: String) {
        val textView = TextView(requireContext()).apply {
            this.text = text
            this.textSize = 16f // 원하는 텍스트 크기
            this.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        scrollViewContainer.addView(textView)
    }

}

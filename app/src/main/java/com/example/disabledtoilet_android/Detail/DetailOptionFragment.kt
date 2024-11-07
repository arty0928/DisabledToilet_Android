package com.example.disabledtoilet_android.Detail

import ToiletModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.disabledtoilet_android.R
//import com.example.disabledtoilet_android.ToiletSearch.Model.ToiletModel
import com.example.disabledtoilet_android.databinding.FragmentDetailOptionBinding
import java.lang.reflect.Field

class DetailOptionFragment : Fragment() {

    // ViewBinding 객체 선언
    private var _binding: FragmentDetailOptionBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // binding 객체를 초기화
        _binding = FragmentDetailOptionBinding.inflate(inflater, container, false)

        binding.root.setBackgroundColor(android.graphics.Color.TRANSPARENT)

        // 전달받은 화장실 데이터
        val toiletData = arguments?.getParcelable<ToiletModel>("TOILET_DATA")

        toiletData?.let { toilet ->
            // 화장실 정보 표시
            binding.toiletName.text = toilet.restroom_name
            binding.toiletOpeningHours.text = toilet.opening_hours
            binding.toiletLocationAddress.text = toilet.address_road
            binding.toiletManageOfficeName.text = toilet.management_agency_name
            binding.toiletManageOfficeNumber.text = toilet.phone_number?.replace("[\"']".toRegex(), "") ?: ""

            // 남성 화장실 정보 표시
            val maleContentLinear = binding.toiletInfoManListLinear
            addToiletInfo(maleContentLinear, "화장실 개수", toilet.male_toilet_count)
            addToiletInfo(maleContentLinear, "소변기 개수", toilet.male_urinal_count)
            addToiletInfo(maleContentLinear, "장애인용 화장실 개수", toilet.male_disabled_toilet_count)
            addToiletInfo(maleContentLinear, "장애인용 소변기 개수", toilet.male_disabled_urinal_count)

            val femaleContentLinear = binding.toiletInfoWomanListLinear
            // 여성 화장실 정보 표시 (필요한 경우)
            addToiletInfo(femaleContentLinear, "화장실 개수", toilet.female_toilet_count)
            addToiletInfo(femaleContentLinear, "장애인용 화장실 개수", toilet.female_disabled_toilet_count)
            addToiletInfo(femaleContentLinear, "어린이용 화장실 개수", toilet.female_child_toilet_count)
            

            // 기타 정보 표시
            binding.toiletManageOfficeName.text = if (toilet.management_agency_name.isNullOrBlank() ||
                toilet.management_agency_name == "\"" ||
                toilet.management_agency_name == "\"\"" ||
                toilet.management_agency_name == "") {
                "정보 없음"
            } else {
                toilet.management_agency_name
            }

            binding.toiletOpeningHours.text = if (toilet.opening_hours_detail.isNullOrBlank() ||
                toilet.opening_hours_detail == "\"" ||
                toilet.opening_hours_detail == "\"\"" ||
                toilet.opening_hours_detail == "") {
                "정보 없음"
            } else {
                toilet.opening_hours_detail
            }


        }

        return binding.root

        // root 뷰를 반환
    }

    private fun addToiletInfo(container: ViewGroup, optionName: String, optionValue: Int?) {
        if (optionValue != null && optionValue > 0) {
            val itemView = layoutInflater.inflate(R.layout.fragment_toilet_detail_content, container, false)
            itemView.findViewById<TextView>(R.id.toilet_detail_option_name).text = optionName
            itemView.findViewById<TextView>(R.id.toilet_detail_option_num).text = optionValue.toString()
            container.addView(itemView)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // 메모리 누수를 방지하기 위해 binding 객체를 해제
        _binding = null
    }

    // male_로 시작하는 필드명에 맞는 옵션 이름을 반환하는 함수
    private fun getOptionName(field: Field): String {
        return when (field.name) {
            "male_toilet_count" -> "남성 화장실 개수"
            "male_urinal_count" -> "남성 소변기 개수"
            "male_disabled_toilet_count" -> "장애인용 화장실 개수"
            "male_disabled_urinal_count" -> "장애인용 소변기 개수"
            "male_child_toilet_count" -> "남자 어린이용 화장실 개수"
            "male_child_urinal_count" -> "남자 어린이용 소변기 개수"

            "female_toilet_count" -> "여성 화장실 개수"
            "female_disabled_toilet_count" -> "여성 장애인용 화장실 개수"
            "female_child_toilet_count" -> "여성 어린이용 화장실 개수"

            else -> "Unknown Option"
        }
    }
}

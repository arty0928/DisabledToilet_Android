package com.example.disabledtoilet_android.Detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.disabledtoilet_android.R
import com.example.disabledtoilet_android.ToiletSearch.Model.ToiletModel
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
        // 화장실 데이터 가져오기
        val toiletMaleData = listOf(
            ToiletModel(
                number = 2,
                category = "카테고리",
                basis = "",
                restroom_name = "에버랜드 놀이공원",
                address_road = "경기도 용인시 48",
                address_lot = "1",

                male_toilet_count = 2,
                male_urinal_count = 1,
                male_disabled_toilet_count = 1,
                male_disabled_urinal_count = 1,

                management_agency_name = "관리주소명 ~~~",
                restroom_ownership_type = "restroom_ownership_type ~~",

                waste_disposal_method = "",
                safety_management_facility_installed = "",
                emergency_bell_installed = "",
                emergency_bell_location = "",
                restroom_entrance_cctv_installed = "",
                diaper_change_table_available = "",
                diaper_change_table_location = "",
                data_reference_date = "",

                opening_hours_detail = "8:00 - 22:00",
                opening_hours = "",

                installation_date = "",
                phone_number= "",
                remodeling_date = "",
                wgs84_latitude= 0.0,  // Double -> String
                wgs84_longitude= 0.0,

            )
        )

        val maleContentLinear = binding.toiletInfoManListLinear
        Log.d("DetailOptionFragment", "LinearLayout ID: $maleContentLinear")

        // 데이터 추가
        for (content in toiletMaleData) {
            val fields = ToiletModel::class.java.declaredFields

            for (field in fields) {
                field.isAccessible = true

                Log.d("DetailOptionFragment", field.name)

                if (field.name.startsWith("male_")) {
                    val value = field.get(content)
                    if (value is Int && value > 0) {
                        val optionName = getOptionName(field)
                        val optionNum = value.toString()

                        Log.d("DetailOptionFragment", "View added. Total views: " + maleContentLinear.childCount)

                        // View를 inflate 할 때 attachToRoot를 false로 설정
                        val itemView = layoutInflater.inflate(
                            R.layout.fragment_toilet_detail_content,
                            maleContentLinear,
                            false
                        )

                        val title = itemView.findViewById<TextView>(R.id.toilet_detail_option_name)
                        val num = itemView.findViewById<TextView>(R.id.toilet_detail_option_num)

                        // TextView에 내용 설정
                        title.text = optionName
                        num.text = optionNum

                        // LinearLayout에 View 추가
                        maleContentLinear.addView(itemView)
                        Log.d("DetailOptionFragment", "View added to LinearLayout")
                    }
                }
            }
        }

        // root 뷰를 반환
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 메모리 누수를 방지하기 위해 binding 객체를 해제
        _binding = null
    }

    // male_로 시작하는 필드명에 맞는 옵션 이름을 반환하는 함수
    private fun getOptionName(field: Field): String {
        return when (field.name) {
            "male_toilet_count" -> "화장실 개수"
            "male_urinal_count" -> "소변기 개수"
            "male_disabled_toilet_count" -> "장애인용 화장실 개수"
            "male_disabled_urinal_count" -> "장애인용 소변기 개수"
            else -> "Unknown Option"
        }
    }
}

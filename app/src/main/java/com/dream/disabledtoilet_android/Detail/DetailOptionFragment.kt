package com.dream.disabledtoilet_android.Detail

import ToiletModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dream.disabledtoilet_android.R
import com.dream.disabledtoilet_android.User.ViewModel.UserViweModel
import com.dream.disabledtoilet_android.Utility.Dialog.SaveManager
import com.dream.disabledtoilet_android.Utility.Dialog.dialog.LoadingDialog
import com.dream.disabledtoilet_android.databinding.FragmentDetailOptionBinding

class DetailOptionFragment : Fragment() {

    private val TAG = "DetailOptionFragment"

    private var _binding: FragmentDetailOptionBinding? = null
    private val binding get() = _binding!!
    private lateinit var saveManager: SaveManager
    val loadingDialog = LoadingDialog()
    private lateinit var scrollViewContainer: LinearLayout
    lateinit var userViewModel : UserViweModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(requireActivity())[UserViweModel::class.java]
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

        scrollViewContainer = binding.filterOptionContainer

        toiletData?.let { toilet ->

            filterOptionAddScrollView(toilet)

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
                "-"
            } else {
                toilet.management_agency_name
            }

            binding.toiletOpeningHours.text = if (toilet.opening_hours_detail.isNullOrBlank() ||
                toilet.opening_hours_detail == "\"" ||
                toilet.opening_hours_detail == "\"\"" ||
                toilet.opening_hours_detail == "") {
                "-"
            } else {
                toilet.opening_hours_detail
            }

            val save_count = binding.toiletSaveCount
            save_count.text = "저장 (${toilet.save})"

            //TODO: 사용자가 좋아요 눌렀으면 TOGGLE 좋아요 표시
            val save_icon = binding.iconToggle

            if(toilet.save > 0){
                save_icon.setImageResource(R.drawable.saved_star_icon)
            }

            // SaveManager 초기화
            saveManager = SaveManager(requireContext()) // requireContext()로 초기화

            // Save 버튼 클릭 리스너 추가
            save_icon.setOnClickListener {
                Log.d(TAG, "눌림")
                saveManager.toggleIcon2(binding.root, toilet) // ToiletManager의 toggleIcon 호출
            }
        }

        return binding.root

        // root 뷰를 반환
    }

    //해당 화장실의 조건 스크롤 뷰
    private fun filterOptionAddScrollView(toilet : ToiletModel){

        if (toilet.emergency_bell_location == "Y") {
            addTextView("비상벨")
        }

        if (toilet.restroom_entrance_cctv_installed == "Y") {
            addTextView("입구 CCTV 설치")
        }

        //공중화장실 or 개방화장실
        if (toilet.category != ""){
            addTextView(toilet.category)
        }

        //민간소유
        if(toilet.restroom_ownership_type.contains("민간소유")){
            addTextView("민간 소유")
        }

        //공공기관 소유
        if(toilet.restroom_ownership_type.contains("공공기관")){
            addTextView("공공기관")
        }

        if (toilet.male_disabled_toilet_count > 0) {
            addTextView("남성 장애인용 화장실")
        }

        if (toilet.male_disabled_urinal_count > 0) {
            addTextView("남성 장애인용 소변기")
        }

        if (toilet.female_disabled_toilet_count > 0) {
            addTextView("여성 장애인용 화장실")
        }

    }

    private fun addToiletInfo(container: ViewGroup, optionName: String, optionValue: Int?) {
        if (optionValue != null && optionValue > 0) {
            val itemView = layoutInflater.inflate(R.layout.fragment_toilet_detail_content, container, false)
            itemView.findViewById<TextView>(R.id.toilet_detail_option_name).text = optionName
            itemView.findViewById<TextView>(R.id.toilet_detail_option_num).text = optionValue.toString()
            container.addView(itemView)
        }
    }

    //option scrollView 추가
    private fun addTextView(optionText: String) {
        // fragment_filter_option을 inflate
        val optionView = layoutInflater.inflate(R.layout.fragment_filter_option, null)

        // optionTextView의 텍스트를 설정
        val optionViewText = optionView.findViewById<TextView>(R.id.optionTextView)
        optionViewText.text = optionText

        // scrollViewContainer에 추가
        scrollViewContainer.addView(optionView)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // 메모리 누수를 방지하기 위해 binding 객체를 해제
        _binding = null
    }


}

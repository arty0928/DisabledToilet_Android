package com.example.disabledtoilet_android

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.widget.LinearLayout
import android.widget.TextView

class ExpandedDetailBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("ExpandedDetailBottomSheet","ExpandedDetailBottomSheet")

        val view = inflater.inflate(R.layout.expanded_detail_bottomsheet, container, false)

        // Info_man_list 레이아웃 가져오기
        val infoManList = view.findViewById<LinearLayout>(R.id.Info_man_list)
        val infoWomanList = view.findViewById<LinearLayout>(R.id.Info_woman_list)

        // 동적으로 아이템 추가
        addInfoItem(infoManList, "소변기수", "2")
        addInfoItem(infoManList, "화장실 위치", "1층")
        addInfoItem(infoWomanList, "좌변기 수", "3")
        addInfoItem(infoWomanList, "화장실 위치", "2층")

        // 추가적인 동적 데이터 예시
        val dynamicData = fetchDynamicData()
        if (dynamicData != null) {
            for (data in dynamicData) {
                Log.d("ExpandedDetail", "Adding data: ${data.name} - ${data.count}")
                when (data.type) {
                    "man" -> addInfoItem(infoManList, data.name, data.count)
                    "woman" -> addInfoItem(infoWomanList, data.name, data.count)
                }
            }
        } else {
            Log.d("ExpandedDetail", "fetchDynamicData returned null")
        }

        return view
    }

    private fun addInfoItem(parent: LinearLayout, infoName: String, infoCount: String) {
        // info_item.xml을 LayoutInflater로 확장
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.toilet_info, parent, false)

        // info_name과 info_count 값을 설정
        val infoNameTextView = itemView.findViewById<TextView>(R.id.info_name)
        val infoCountTextView = itemView.findViewById<TextView>(R.id.info_count)

        infoNameTextView.text = infoName
        infoCountTextView.text = infoCount

        // 부모 레이아웃에 추가
        parent.addView(itemView)
    }

    // 동적 데이터를 가져오는 함수 (예시)
    private fun fetchDynamicData(): List<ToiletInfo>? {
        // 이 함수는 실제 데이터를 가져오는 로직으로 대체되어야 합니다.
        // 예시로 가상 데이터를 반환합니다.
        return listOf(
            ToiletInfo("man", "소변기 수", "3"),
            ToiletInfo("woman", "좌변기 수", "4")
        )
    }
}

data class ToiletInfo(val type: String, val name: String, val count: String)

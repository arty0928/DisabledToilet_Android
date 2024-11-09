package com.example.disabledtoilet_android.Utility.Dialog.utils

import ToiletModel
import android.util.Log

/**
 * 데이터 처리 작업을 위한 유틸리티 함수 제공 (예: 빈 항목 제거)
 */
object DataUtils {

    /**
     * 비어있는 화장실 이름을 가진 항목을 리스트에서 제거
     * @param toiletList 필터링할 ToiletModel 객체의 리스트
     * @return 비어있지 않은 화장실 이름을 가진 리스트
     */
    fun removeEmptyData(toiletList: MutableList<ToiletModel>): MutableList<ToiletModel> {
        for (i in toiletList.size - 1 downTo 0) {
            val toiletName = toiletList[i].restroom_name
            if (toiletName == "") {
                Log.d("DataUtils", "Removing: ${toiletList[i]}")
                toiletList.removeAt(i)
            }
        }
        return toiletList
    }
}
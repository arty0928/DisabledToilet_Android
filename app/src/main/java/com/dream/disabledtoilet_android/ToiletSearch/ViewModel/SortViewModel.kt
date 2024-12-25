package com.dream.disabledtoilet_android.ToiletSearch.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * SortDialog에서 사용하는 ViewModel
 */
class SortViewModel : ViewModel() {

    //SortString dataClass 초기화
    val sortString = SortString()
    // Dialog 현재 띄워져있는지
    val isDialogDismissed = MutableLiveData<Boolean>()

    // Sort 데이터
    val SortCheck = MutableLiveData<Int>()

    //storeData() 호출 시 데이터 상태 저장 정보
    private var storeStatus : SortStatus? = null

    init {
        SortCheck.value = sortString.sortByDistance
    }

    /**
     *  저장된 값 이용하기 위한 data class
     */
    data class SortString(
        val sortByDistance: Int = 0,
        val sortBySaved: Int = 1
    )

    /**
     * Sort 조건 적용에 사용되는 데이터 클래스
     */
    data class SortModel(
        var sortName : String,
        var checked: Boolean
    )

    /**
     * Sort 상태 저장을 위한 데이터 클래스
     */
    data class SortStatus(
        val sortCheckedStatus : Int
    )

    /**
     * Sort Dialog show 되었을 때 값 store
     */
    fun storeStatus() {
         storeStatus = SortStatus(
             sortCheckedStatus = SortCheck.value ?: sortString.sortByDistance
        )
    }

    /**
     * storeStatus() 호출 당시 데이터로 load
     */
    fun loadStatus(){
        storeStatus?.let{status ->
            SortCheck.value = status.sortCheckedStatus
        }
    }
}

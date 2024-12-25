package com.dream.disabledtoilet_android.ToiletSearch.SearchFilter.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


/**
 * FilterSearchDialog에 사용하는 ViewModel
 * FilterString을 통해서 조건 적용에 들어가는 텍스트까지 관리
 */
class FilterViewModel : ViewModel() {
    // filterString dataClass 초기화
    val filterString = FilterString()
    // Dialog 현재 띄워져있는지 데이터
    val isDialogDismissed = MutableLiveData<Boolean>()
    // 화장실 최근 점검 데이터
    val toiletRecentCheck = MutableLiveData<Int>()
    // 현재 운영 데이터
    val isToiletOperating = MutableLiveData<Boolean>()
    // 조건 적용 데이터 리스트
    private var filterList = mutableListOf<FilterModel>()
    // 조건 적용 데이터 라이브 데이터
    val filterLiveList = MutableLiveData<MutableList<FilterModel>>()
    //storeData() 호출 시 데이터 상태 저장 정보
    private var savedStatus: FilterStatus? = null

    init {
        // filterLiveList 초기화
        filterLiveList.value = filterList
        // filterList 초기화
        for (i in 0 until filterString.filterNameList.size) {
            filterList.add(
                FilterModel(
                    filterString.filterNameList[i],
                    false
                )
            )
        }
        toiletRecentCheck.value = filterString.toiletCheckNever
        isToiletOperating.value = false
    }
    /**
     * 조건 적용에서 update를 위한 함수
     */
    fun updateFilterCheck(index: Int, isChecked: Boolean) {
        filterList[index].checked = isChecked
        filterLiveList.value = filterList
    }
    /**
     * 지정된 값 이용하기 위한 data class
     */
    data class FilterString(
        val toiletCheckNever: Int = 0,
        val toiletCheckInYear: Int = 1,
        val toiletCheckHalfYear: Int = 3,
        val toiletCheckInMonth: Int = 4,
        // 필터 이름(조건적용) 리스트
        val filterNameList: List<String> = listOf(
            "장애인 소변기",
            "장애인 대변기",
            "비상벨",
            "입구 CCTV",
            "개방화장실",
            "공중화장실",
            "민간소유",
            "공공기관"
        )
    )
    /**
     * 조건 적용에 사용되는 데이터 클래스
     */
    data class FilterModel(
        var filterName: String,
        var checked: Boolean
    )
    /**
     *  필터 상태 저장을 위한 데이터 클래스
     */
    data class FilterStatus(
        val filterCheckedStates: List<Boolean>,
        val toiletRecentCheckValue: Int,
        val isToiletOperatingValue: Boolean
    )
    /**
     * 조건 검색 show 되었을 때 값 store
     */
    fun storeStatus() {
        savedStatus = FilterStatus(
            filterCheckedStates = filterList.map { it.checked },
            toiletRecentCheckValue = toiletRecentCheck.value ?: filterString.toiletCheckNever,
            isToiletOperatingValue = isToiletOperating.value ?: false
        )
    }
    /**
     * storeStatus() 호출 당시 데이터로 load
     */
    fun loadStatus() {
        savedStatus?.let { status ->
            // filterList 복원
            status.filterCheckedStates.forEachIndexed { index, checked ->
                filterList[index].checked = checked
            }
            filterLiveList.value = filterList

            // 다른 상태값들 복원
            toiletRecentCheck.value = status.toiletRecentCheckValue
            isToiletOperating.value = status.isToiletOperatingValue
        }
    }
}

data class FilterState(
    val filterCheckedStates: List<Boolean> = listOf(),
    val toiletRecentCheckValue: Int = 0,
    val isToiletOperatingValue: Boolean = false
)
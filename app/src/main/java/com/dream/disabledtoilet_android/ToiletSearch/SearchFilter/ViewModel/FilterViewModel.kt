package com.dream.disabledtoilet_android.ToiletSearch.SearchFilter.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dream.disabledtoilet_android.ToiletSearch.SearchFilter.DataLayer.OptionBuilder
import com.dream.disabledtoilet_android.ToiletSearch.SearchFilter.Model.OptionModel


/**
 * FilterSearchDialog에 사용하는 ViewModel
 * FilterString을 통해서 조건 적용에 들어가는 텍스트까지 관리
 */
class FilterViewModel : ViewModel() {
    val _filterStatus = MutableLiveData<FilterStatus>()
    val filterStatus: LiveData<FilterStatus> get() = _filterStatus

    val _recentCheck = MutableLiveData<Int>()
    val recentCheck: LiveData<Int> get() = _recentCheck

    val _optionStatus = MutableLiveData<List<OptionModel>>()
    val optionStatus: LiveData<List<OptionModel>> get() = _optionStatus

    private var originalFilterStatus = FilterStatus(
        recentCheck = RecentCheckStatus(0),
        optionStatus = OptionStatus()
    )

    init {
        _recentCheck.value = 0
        _optionStatus.value = OptionBuilder().buildOptionList()
        _filterStatus.value = FilterStatus(
            recentCheck = RecentCheckStatus(0),
            optionStatus = OptionStatus()
        )
    }

    /**
     * 원래 값 저장
     */
    fun saveOriginalFilterStatus(value: FilterStatus){
        originalFilterStatus = value
        Log.d("test saveOrigin1", originalFilterStatus.toString())
        applyOriginalFilterStatus()
    }

    /**
     * 원래 값 get
     */
    fun getOriginalFilterStatus(): FilterStatus {
        Log.d("test saveOrigin2", originalFilterStatus.toString())
        return originalFilterStatus
    }

    /**
     * 현재 필터값 get
     */
    fun getFilterStatus(): FilterStatus {
        return FilterStatus(
            recentCheck = RecentCheckStatus(recentCheck.value!!),
            optionStatus = OptionStatus(optionStatus.value!!)
        )
    }

    /**
     * 최근 점검값 세팅
     */
    fun setRecentCheck(value: Int) {
        _recentCheck.value = value
    }

    /**
     * 옵션값 세팅
     */
    fun setOptionStatus(value: List<OptionModel>){
        _optionStatus.value = value
    }
    /**
     * 필터값 초기화
     */
    fun clearFilterStatus(){
        setRecentCheck(0)
        setOptionStatus(OptionBuilder().buildOptionList())
    }
    /**
     * 필터값 생성
     */
    fun makeFilterStatus(){
        _filterStatus.value = FilterStatus(
            recentCheck = RecentCheckStatus(recentCheck.value!!),
            optionStatus = OptionStatus(optionStatus.value!!)
        )
    }
    /**
     * 초기값 적용
     */
    private fun applyOriginalFilterStatus(){
        setOptionStatus(originalFilterStatus.optionStatus.optionStatusList)
        setRecentCheck(originalFilterStatus.recentCheck.value)
    }

}

data class FilterStatus(
    val recentCheck: RecentCheckStatus,
    val optionStatus: OptionStatus
)

data class RecentCheckStatus(
    val value: Int = 0
)

data class OptionStatus(
    val optionStatusList: List<OptionModel> = OptionBuilder().buildOptionList()
)
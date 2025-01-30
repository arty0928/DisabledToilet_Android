package com.dream.disabledtoilet_android.ToiletSearch.ViewModel

import com.dream.disabledtoilet_android.Model.ToiletModel
import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.dream.disabledtoilet_android.ToiletSearch.SearchFilter.ViewModel.FilterStatus
import com.dream.disabledtoilet_android.ToiletSearch.SearchFilter.ViewModel.OptionStatus
import com.dream.disabledtoilet_android.ToiletSearch.SearchFilter.ViewModel.RecentCheckStatus
import com.dream.disabledtoilet_android.ToiletSearch.ToiletData
import com.dream.disabledtoilet_android.ToiletSearch.ToiletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ToiletSearchViewModel: ViewModel() {
    @SuppressLint("NewApi")
    val toiletRepository = ToiletRepository()

    private val _toiletListState = MutableStateFlow(ToiletListState())
    val toiletListState = _toiletListState.asStateFlow()
    private val _filterDialogStatus = MutableStateFlow(FilterDialogStatus())
    val filterDialogStatus = _filterDialogStatus.asStateFlow()
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    init {
        _toiletListState.value = ToiletListState(
            filteredToiletList = ToiletData.cachedToiletList!!,
            cachedToiletList = ToiletData.cachedToiletList!!
        )

        _filterDialogStatus.value = FilterDialogStatus(
            isDismissed = true,
            filterStatus = FilterStatus(
                RecentCheckStatus(),
                OptionStatus()
            )
        )
    }

    fun setCachedToiletList(toiletList: List<ToiletModel>) {
        _toiletListState.value = ToiletListState(
            filteredToiletList = _toiletListState.value.filteredToiletList,
            cachedToiletList = toiletList
        )
    }

    fun setFilteredToiletList(toiletList: List<ToiletModel>) {
        _toiletListState.value = ToiletListState(
            filteredToiletList = toiletList,
            // 화장실 리스트 캐시는 그대로 유지
            cachedToiletList = _toiletListState.value.cachedToiletList
        )
    }

    @SuppressLint("NewApi")
    fun setFilterDialogStatus(filterDialogStatus: FilterDialogStatus){
        _filterDialogStatus.value = filterDialogStatus
        // 바로 필터링 된 화장실 리스트 세팅
        setFilteredToiletList(getFilteredToiletList())
    }

    fun setIsDialogDismissed(isDismissed: Boolean){
        _filterDialogStatus.value = FilterDialogStatus(
            isDismissed = isDismissed,
            // 필터 값은 그대로 유지
            filterStatus = _filterDialogStatus.value.filterStatus
        )
    }

    @SuppressLint("NewApi")
    fun getSearchedToiletList(): List<ToiletModel> {
        return toiletRepository.getToiletWithSearchKeyword(
            toiletListState.value.filteredToiletList,
            query.value
        )
    }

    fun setQuery(query: String) {
        _query.value = query
    }

    @SuppressLint("NewApi")
    private fun getFilteredToiletList(): List<ToiletModel> {
        return toiletRepository.setFilteredToiletList(
            filterDialogStatus.value.filterStatus,
            toiletListState.value.cachedToiletList.toList()
        )
    }
}

data class ToiletListState(
    val filteredToiletList: List<ToiletModel> = emptyList(),
    val cachedToiletList: List<ToiletModel> = emptyList(),
)

data class FilterDialogStatus(
    val isDismissed: Boolean = true,
    val filterStatus: FilterStatus = FilterStatus(
        RecentCheckStatus(),
        OptionStatus()
    )
)
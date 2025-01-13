package com.dream.disabledtoilet_android.ToiletSearch

import com.dream.disabledtoilet_android.ToiletSearch.SearchFilter.ViewModel.FilterStatus

interface FilterApplyListener {
    fun onApplyFilterListener(filterStatus: FilterStatus)
    fun onDialogDismissListener(isDismissed: Boolean)
}
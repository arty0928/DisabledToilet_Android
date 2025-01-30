package com.dream.disabledtoilet_android.Utility.Dialog.SearchDialog.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dream.disabledtoilet_android.Model.PlaceModel

class SearchDialogViewModel: ViewModel() {
    private val _query = MutableLiveData<String>("")
    val query: LiveData<String> get() = _query

    private val _searchResult = MutableLiveData<List<PlaceModel>>()
    val searchResult: LiveData<List<PlaceModel>> get() = _searchResult

    fun setQuery(query: String) {
        _query.value = query
    }

    fun setSearchResult(searchResult: List<PlaceModel>) {
        _searchResult.value = searchResult
    }
}
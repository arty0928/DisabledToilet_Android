package com.dream.disabledtoilet_android.Utility.Dialog.SearchDialog.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dream.disabledtoilet_android.Utility.KaKaoAPI.Model.SearchResultDocument

class SearchDialogViewModel: ViewModel() {
    private val _query = MutableLiveData<String>("")
    val query: LiveData<String> get() = _query

    private val _searchResult = MutableLiveData<List<SearchResultDocument>>()
    val searchResult: LiveData<List<SearchResultDocument>> get() = _searchResult

    fun setQuery(query: String) {
        _query.value = query
    }

    fun setSearchResult(searchResult: List<SearchResultDocument>) {
        _searchResult.value = searchResult
    }


}
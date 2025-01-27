package com.dream.disabledtoilet_android.Utility.Dialog.SearchDialog.Listener

import com.dream.disabledtoilet_android.Utility.KaKaoAPI.Model.SearchResultDocument

interface SearchResultSelectListener {
    fun onSearchResultSelected(searchResult: SearchResultDocument)
}
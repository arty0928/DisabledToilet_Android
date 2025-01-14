package com.dream.disabledtoilet_android.ToiletSearch.Adapter

import com.dream.disabledtoilet_android.ToiletSearch.SearchFilter.Model.OptionModel

interface AdapterEventListener {
    fun onOptionCheckedChange(optionList: List<OptionModel>)
}
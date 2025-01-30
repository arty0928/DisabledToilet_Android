package com.dream.disabledtoilet_android.ToiletSearch.Adapter

import com.dream.disabledtoilet_android.Model.OptionModel

interface AdapterEventListener {
    fun onOptionCheckedChange(optionList: List<OptionModel>)
}
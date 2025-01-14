package com.dream.disabledtoilet_android.ToiletSearch.SearchFilter.DataLayer

import com.dream.disabledtoilet_android.ToiletSearch.SearchFilter.Model.OptionModel

class OptionBuilder {
    fun buildOptionList(): List<OptionModel>{
        val optionList = mutableListOf<OptionModel>(
            OptionModel(OptionStringList().disabledUrinal, false),
            OptionModel(OptionStringList().disabledToilet, false),
            OptionModel(OptionStringList().emergencyBell, false),
            OptionModel(OptionStringList().entranceCCTV, false),
        )

        return optionList
    }
}

data class OptionStringList(
    val genderSeparation: String = "남녀 구분",
    val disabledUrinal: String = "장애인 소변기",
    val disabledToilet: String = "장애인 대변기",
    val emergencyBell: String = "비상벨",
    val entranceCCTV: String = "입구 CCTV",
)
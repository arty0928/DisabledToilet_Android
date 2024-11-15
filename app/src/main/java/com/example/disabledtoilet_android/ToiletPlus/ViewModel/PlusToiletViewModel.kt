package com.example.disabledtoilet_android.ToiletPlus.ViewModel

import androidx.lifecycle.ViewModel

/**
 * 화장실 등록 상세 정보 뷰모델
 */
class PlusToiletViewModel(): ViewModel() {
    data class statusString(
        val firstStatus: String = "",

    )
}
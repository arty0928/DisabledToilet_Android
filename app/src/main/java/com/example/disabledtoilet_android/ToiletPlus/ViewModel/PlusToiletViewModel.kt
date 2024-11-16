package com.example.disabledtoilet_android.ToiletPlus.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.tools.build.jetifier.core.utils.Log

/**
 * 화장실 등록 상세 정보 뷰모델
 */
class PlusToiletViewModel(): ViewModel() {
    private val Tag = "test log"
    // 상태 이름 여기서 받아서 사용
    val statusStringList = listOf<String>(
        "장애인 소변기",
        "장애인 대변기",
        "비상벨",
        "입구 CCTV",
        "개방 화장실",
        "공중 화장실",
        "남녀 구분",
        "손잡이",
    )
    // 화장실 주소
    val toiletAddress = MutableLiveData<String>()
    // 화장실 이름
    private val toiletName = MutableLiveData<String>()
    // 화장실 상태 선택 리스트
    val toiletStatusList = MutableLiveData<List<ToiletStatusModel>>()
    /**
     * 생성 시 Value 세팅
     */
    init {
        setToiletAddress("")
        setToiletName("")
        setToiletStatusList(initToiletStatusList())
    }
    /**
     * 화장실 상태 선택 데이터 클래스
     */
    data class ToiletStatusModel(
        var statusName: String,
        var status: Boolean
    )
    /**
     * 화장실 주소 업데이트
     */
    fun setToiletAddress(address: String){
        toiletAddress.value = address
        Log.d(Tag, "[viewModel] toiletAddress updated: $address")
    }
    /**
     * 화장실 이름 업데이트
     */
    fun setToiletName(name: String){
        toiletName.value = name
        Log.d(Tag, "[viewModel] toiletName updated: $name")
    }
    /**
     * 화장실 상태 선택 리스트 세팅
     */
    fun setToiletStatusList(statusList: List<ToiletStatusModel>){
        toiletStatusList.value = statusList
        if (statusList.size != 8){
            Log.e(Tag, "statusList size error: ${statusList.size}")
        } else {
            Log.d(Tag, "statusList size set")
        }
    }
    /**
     * 화장실 상태 선택 초기 리스트 생성
     */
    fun initToiletStatusList(): List<ToiletStatusModel>{
        val toiletStatusList = mutableListOf<ToiletStatusModel>()
        for (i in statusStringList.indices){
            toiletStatusList.add(ToiletStatusModel(statusStringList[i],false))
        }
        return toiletStatusList.toList()
    }
}
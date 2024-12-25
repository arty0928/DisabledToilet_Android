package com.dream.disabledtoilet_android.Near.UILayer.ViewModel

import ToiletModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dream.disabledtoilet_android.Near.DataLayer.LabelBuilder
import com.dream.disabledtoilet_android.Near.DataLayer.ToiletListGenerator
import com.dream.disabledtoilet_android.Near.DomainLayer.NearDomain
import com.dream.disabledtoilet_android.ToiletSearch.SearchFilter.ViewModel.FilterState
import com.dream.disabledtoilet_android.ToiletSearch.ToiletData
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.camera.CameraPosition
import com.kakao.vectormap.label.Label

class NearViewModel: ViewModel() {
    val nearDomain = NearDomain()
    val toiletListGenerator = ToiletListGenerator()
    // 맵 상태
    private val _mapState = MutableLiveData<MapStatus>()
    val mapState: LiveData<MapStatus> get() = _mapState
    // UI 상태
    private val _uiState = MutableLiveData<UIStatus>()
    val uiState: LiveData<UIStatus> get() = _uiState
    // 지도 초기화 상태
    private val _isMapInit = MutableLiveData<Boolean>()
    val isMapInit: LiveData<Boolean> get() = _isMapInit
    // 카메라 상태
    private val _cameraPosition = MutableLiveData<CameraPosition>()
    val cameraPosition: LiveData<CameraPosition> get() = _cameraPosition
    // 내 위치
    private val _myLocation = MutableLiveData<LatLng>()
    val myLocation: LiveData<LatLng> get() = _myLocation

    init {
        val mapStatus = MapStatus(
            ToiletData.cachedToiletList!!,
            listOf(),
            ToiletData.cachedToiletList.isNullOrEmpty(),
            false,
            listOf()
        )
        val uiStatus = UIStatus(
            false,
            false
        )
        _mapState.value = mapStatus
        _uiState.value = uiStatus
    }
    /**
     * 내 위치 세팅
     */
    fun setMyLocation(location: LatLng){
        Log.d("test log", "setMyLocation: $location")
        _myLocation.value = location
    }
    /**
     * 현재 필터링 된 화장실 리스트 기반으로 레이블 리스트 생성
     * 지도 초기화 된 후 사용해야 함
     */
    fun makeToiletLabelList(kakaoMap: KakaoMap){
        Log.d("test log", "makeToiletLabelList")
        // 카카오맵 객체가 필요하기 때문에 지도 초기화 된 후 사용
        Log.d("test log", "makeToiletLabelList: kakaoMap initialized")
        val labelBuilder = LabelBuilder(kakaoMap)
        Log.d("test log",mapState.value!!.filteredToiletList.size.toString())
        _mapState.value = mapState.value?.copy(
            toiletLabelList = labelBuilder.makeToiletLabelList(
                mapState.value!!.filteredToiletList
            )
        )
    }
    /**
     * 카카오 맵이 세팅되면 isMapInit 상태 변경
     */
    fun setIsMapInit(status: Boolean){
        _isMapInit.value = status
    }
    /**
     * 필터 상태 적용
     */
    fun setFilter(filterState: FilterState){
        _mapState.value = mapState.value?.copy(
            filteredToiletList = ToiletData.cachedToiletList!!.toList()
        )
        Log.d("test log", "남은 화장실 수: ${mapState.value!!.filteredToiletList.size}")
    }
    /**
     * 현재 카메라 위치 기반으로 20km 이내의 화장실 레이블 리스트 받아오기
     */
    fun getToiletLabelListInCamera(kakaoMap: KakaoMap): List<Label>{
        val labelBuilder = LabelBuilder(kakaoMap)
        val toiletListInCamera = toiletListGenerator.makeToiletListInCamera(
            cameraPosition.value!!,
            mapState.value!!.filteredToiletList
        )
        return labelBuilder.makeToiletLabelList(toiletListInCamera)
    }
    /**
     * 현재 카메라 위치 받기
     */
    fun setCurrentCameraPosition(cameraPosition: CameraPosition){
        _cameraPosition.value = cameraPosition
    }
}

data class MapStatus(
    val toiletList: List<ToiletModel>?,
    val filteredToiletList: List<ToiletModel>,
    val isToiletListEmpty: Boolean,
    val allLabelShowed: Boolean,
    val toiletLabelList: List<Label>
)

data class UIStatus(
    val isLoading: Boolean,
    val isMapLoading: Boolean
)
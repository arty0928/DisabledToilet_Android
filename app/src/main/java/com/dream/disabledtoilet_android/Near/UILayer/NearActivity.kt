package com.dream.disabledtoilet_android.Near.UILayer

import ToiletModel
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dream.disabledtoilet_android.BuildConfig
import com.dream.disabledtoilet_android.Near.UILayer.ViewModel.NearViewModel
import com.dream.disabledtoilet_android.ToiletSearch.SearchFilter.ViewModel.FilterState
import com.dream.disabledtoilet_android.Utility.Dialog.dialog.LoadingDialog
import com.dream.disabledtoilet_android.databinding.ActivityNearBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdate
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Exception
import kotlin.coroutines.resume

@RequiresApi(Build.VERSION_CODES.O)
class NearActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNearBinding
    private val loadingDialog = LoadingDialog()
    private lateinit var kakaoMap: KakaoMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNearBinding.inflate(layoutInflater)
        KakaoMapSdk.init(this, BuildConfig.KAKAO_SCHEME)
        setContentView(binding.root)
        // 뷰모델 받기
        val viewModel = ViewModelProvider(this).get(NearViewModel::class.java)
        // 임시
        viewModel.setFilter(FilterState())
        // 사용자 위치
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 맵뷰 초기화
        binding.mapView.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() {
                    Log.d("test log: Map", "onMapDestroy")
                    viewModel.setIsMapInit(false)
                }
                override fun onMapError(p0: Exception?) {
                    Log.e("test log: Map", "onMapError")
                    viewModel.setIsMapInit(false)
                }
            },object : KakaoMapReadyCallback(){
                override fun onMapReady(map: KakaoMap) {
                    Log.d("test log: Map", "onMapReady")
                    kakaoMap = map
                    viewModel.setIsMapInit(true)
                }
            }
        )

        // 맵뷰 초기화 관측 시
        viewModel.isMapInit.observe(this) { state ->
            // 맵뷰 초기화 됐으면
            if (viewModel.isMapInit.value == true){
                Log.d("test log", "isMapInit: $state observed")

                lifecycleScope.launch {
                    // 현재 위치 받아서 뷰모델에 넣기
                    viewModel.setMyLocation(getMyLocation())
                    // 현재 위치로 카메라 이동
                    val cameraUpdate = CameraUpdateFactory.newCenterPosition(viewModel.myLocation.value, 17)
                    moveCamera(cameraUpdate)
                }

                // 카메라 이동 감지 리스너
                kakaoMap.setOnCameraMoveEndListener { kakaoMap, cameraPosition, gestureType ->
                    // 이동 감지되면 카메라 포지션 뷰모델에 세팅
                    viewModel.setCurrentCameraPosition(cameraPosition)
                }

                // 뷰모델의 카메라 포지션 값 변동 시
                viewModel.cameraPosition.observe(this){ cameraPosition ->
                    // 카메라 포지션 기준 20kM 내의 화장실 레이블 받아서
                    val labelsInCamera = viewModel.getToiletLabelListInCamera(kakaoMap)
                    // 화장실 레이블 지도에 표시
                    showLabelList(labelsInCamera)
                }
            }
        }
    }
    /**
     * 로딩 다이얼로그 띄우기
     */
    private fun showLoading(){
        if (!loadingDialog.isAdded){
            loadingDialog.show(supportFragmentManager, "loading_dialog")
        }
    }
    /**
     * 로딩 다이얼로그 없애기
     */
    private fun dismissLoading(){
        if(loadingDialog.isAdded){
            loadingDialog.dismiss()
        }
    }
    /**
     * 현재 위치 받아오기
     */
    private suspend fun getMyLocation(): LatLng {
        var currentPosition = LatLng.from(0.0, 0.0)
        // 권한 확인
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("test log", "권한 없음")
            return currentPosition // 권한이 없으면 기본값 반환
        }

        // 위치 요청을 위한 코루틴
        currentPosition = suspendCancellableCoroutine { continuation ->
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    currentPosition = LatLng.from(latitude, longitude)
                    Log.d("test log", "현재 위치: $currentPosition")
                    continuation.resume(currentPosition) // 위치 정보가 업데이트되면 코루틴 재개
                } else {
                    Log.d("test log", "위치 정보를 가져올 수 없습니다.")
                    continuation.resume(currentPosition) // 위치 정보가 없을 경우 기본값으로 재개
                }
            }
        }

        return currentPosition
    }
    /**
     * 파라미터 위치로 카메라 이동
     */
    private fun moveCamera(cameraUpdate: CameraUpdate) {
        Log.d("test log", "카메라 이동")
        kakaoMap.moveCamera(cameraUpdate)
    }
    /**
     * Label 리스트의 Label 지도에 띄우기
     */
    private fun showLabelList(labelList: List<Label>){
        var count = 0
        for (i in labelList.indices){
            labelList.get(i).show()
            count ++
        }
        Log.d("test log", "지도에 띄워진 화장실 수: $count")
    }
}
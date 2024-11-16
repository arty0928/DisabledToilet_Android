package com.example.disabledtoilet_android.ToiletPlus

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.disabledtoilet_android.R
import com.example.disabledtoilet_android.Utility.Dialog.dialog.LoadingDialog
import com.example.disabledtoilet_android.databinding.ActivityInputPlusToiletInfoBinding
import com.example.disabledtoilet_android.databinding.ActivityPlusToiletBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.Marker
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraUpdateFactory
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * 화장실 추가 화면
 */
class ToiletPlusActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlusToiletBinding
    private val Tag = "test log"
    // onCreate에서 initail 됐는지 확인 필요
    lateinit var kakaoMap: KakaoMap
    // 로딩 다이얼로그
    val loadingDialog = LoadingDialog()
    // 위치 권한 코드
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlusToiletBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 위치 권한 확인
        if (!isLocationPermissionGranted()) {
            // 권한 없으면 권한 설정
            getLocationPermission()
        }
        //카카오SDK 초기화 이거 앱키 나중에 ignore 작업 해주기
        KakaoMapSdk.init(this, "0da87b34c4becc2c67033fb4c1561bdf")
        // UI 세팅
        setUi()
    }
    /**
     * Request 요청 시, 요청 결과 확인
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            Log.d("test log", "위치 권한 확인")
            lifecycleScope.launch(Dispatchers.Main) {
                // 로딩
                showLoadingDialog()
                // suspend 하면서 위치 정보 받아오기
                val userLocation = getUserLocation()
                // 위치 받기 끝나면 로딩 끄기
                dismissLoadingDialog()
                // 카메라 위치 유저로
                moveMapCameraToUser(userLocation)
            }
        }
    }
    /**
     * UI 세팅
     */
    private fun setUi() {
        // 뒤로가기 버튼
        binding.backButton.setOnClickListener {
            onBackPressed()
        }
        // 확인 버튼 세팅
        setCheckBtn()
        //카카오맵 세팅될 떄까지 로딩화면
        showLoadingDialog()
        // 로딩 화면 보여질 동안 카카오맵 셍팅
        lifecycleScope.launch(Dispatchers.IO) {
            // suspend 하면서 카카오맵 세팅
            setKakaoMap()
            // UI 작업
            withContext(Dispatchers.Main) {
                // suspend 하면서 위치 정보 받아오기
                val userLocation = getUserLocation()
                // 카카오맵 세팅 끝나면 로딩 끄기
                dismissLoadingDialog()
                // 카메라 위치 유저로
                moveMapCameraToUser(userLocation)

            }
        }
    }
    /**
     * 확인 버튼 세팅
     */
    private fun setCheckBtn() {
        val checkBtn = binding.plusToiletCheckButton
        checkBtn.setOnClickListener {
            val aimLocation = getToiletLocation()
            if (aimLocation != null) {
                goToToiletInfoInputActivity(
                    aimLocation.latitude,
                    aimLocation.longitude
                )
            } else {
                Log.d(Tag, "aimLocation is null")
            }
        }
    }
    /**
     * 확인 버튼 선택 시점의 위치 정보 받아오기
     */
    private fun getToiletLocation(): LatLng? {
        // 스크린 중앙의 지리 좌표 구하기
        val centerLocation = makeAimOnMap()
        Log.d(Tag, "선택된 좌표: " + centerLocation.toString())
        return centerLocation
    }
    /**
     * 비동기로 카카오맵 세팅
     */
    private suspend fun setKakaoMap() {
        // 맵뷰 초기화
        initializeMapView()
    }
    /**
     * 비동기로 맵뷰 초기화
     */
    private suspend fun initializeMapView(): Boolean {
        val isSuccess = CompletableDeferred<Boolean>()
        // Main 스레드에서 해야하나...?
        withContext(Dispatchers.Main) {
            binding.mapView.start(object : MapLifeCycleCallback() {
                override fun onMapDestroy() {
                    Log.d(Tag, "MapView destroyed")
                }

                override fun onMapError(error: Exception) {
                    Log.e(Tag, "Map error: ${error.message}")
                    isSuccess.completeExceptionally(error)
                }
            }, object : KakaoMapReadyCallback() {
                override fun onMapReady(map: KakaoMap) {
                    // 카카오맵 초기화 시점
                    kakaoMap = map
                    Log.d(Tag, "KakaoMap is ready")
                    isSuccess.complete(true)
                }
            })
        }
        return isSuccess.await()
    }
    /**
     * 로딩 화면 띄우기
     */
    private fun showLoadingDialog() {
        loadingDialog.show(supportFragmentManager, loadingDialog.tag)
    }
    /**
     * 로딩 화면 끄기
     */
    private fun dismissLoadingDialog() {
        loadingDialog.dismiss()
    }
    /**
     * 현재 위치 비동기로 받아오기
     */
    private suspend fun getUserLocation(): LatLng? {
        var currentPosition: LatLng? = null
        // 권한부터 확인
        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // 권한 있음
            Log.d("test log", "LocationPermission Granted")
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            try {
                // 로케이션 받아올때까지 await()
                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    null
                ).await()
                //currentPosition 생성
                currentPosition = LatLng.from(location.latitude, location.longitude)
            } catch (e: Exception) {
                Log.e("test Log", "Failed to get location: ${e.message}")
                Toast.makeText(this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // 권한 없음
            Log.e("test log", "Location permission not granted")
        }
        Log.d(Tag, "Location: " + currentPosition.toString())
        return currentPosition
    }
    /**
     * 위치 권한 체크
     */
    private fun isLocationPermissionGranted(): Boolean {
        var result = false
        // 위치 권한 값
        val permissionCode = ActivityCompat.checkSelfPermission(
            this,
            ACCESS_FINE_LOCATION
        )
        // 권한 값 체크
        if (permissionCode == PackageManager.PERMISSION_GRANTED) {
            result = true
        }
        return result
    }
    /**
     * 위치 권한 받기
     */
    private fun getLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }
    /**
     * 파라미터 위치로 지도 카메라 옮기기
     */
    private fun moveMapCameraToUser(location: LatLng?) {
        // null값 확인부터
        if (location != null) {
            // 지도의 중심을 화장실 위치로 이동
            if (::kakaoMap.isInitialized) {
                // 여기 비동기 처리하면 바꿔줄 것
                kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(location, 16))
            } else {
                Log.d("test log", "kakaoMap is not initailized")
            }
        }
    }
    /**
     * 지도 가운데 지리 좌표 반환
     */
    private fun makeAimOnMap(): LatLng? {
        val mapView = binding.mapView
        // 맵뷰의 가운대 스크린 좌표 구하기
        val centerX = mapView.left + mapView.width / 2
        val centerY = mapView.top + mapView.height / 2
        // 스크린 중앙 좌표를 통해서 지리 좌표 구하기
        val mapCenterLocation = kakaoMap.fromScreenPoint(centerX, centerY)
        return mapCenterLocation
    }
    /**
     * 좌표 받아서 화장실 추가 상세 화면으로 넘겨주기
     */
    private fun goToToiletInfoInputActivity(latitude: Double, longitude: Double) {
        val intent = Intent(this, InputPlusToiletInputPageActivity::class.java)
        // 좌표 값 넣어서 intent
        intent.putExtra("latitude", latitude)
        intent.putExtra("longitude", longitude)
        startActivity(intent)
    }
}
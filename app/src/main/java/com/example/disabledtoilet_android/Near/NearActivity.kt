package com.example.disabledtoilet_android.Near

import ToiletModel
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.disabledtoilet_android.Detail.DetailPageActivity
import com.example.disabledtoilet_android.R
import com.example.disabledtoilet_android.ToiletSearch.ToiletData
import com.example.disabledtoilet_android.ToiletSearch.ToiletRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kakao.vectormap.*
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.LatLng
import com.google.firebase.FirebaseApp

class NearActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var kakaoMap: KakaoMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    val Tag = "NearActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KakaoMapSdk.init(this, "ce27585c8cc7c468ac7c46901d87199d")
        setContentView(R.layout.activity_near)

        FirebaseApp.initializeApp(this)

        initializeMap() //멘토님 추가

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 위치 권한 체크 및 요청
        checkLocationPermission()

        // 뒤로 가기 버튼 설정
        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initializeBottomSheet() {
        // detail_bottomsheet 레이아웃을 바텀시트로 사용
        val bottomSheetView = layoutInflater.inflate(R.layout.detail_bottomsheet, null)
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        bottomSheetDialog.setContentView(bottomSheetView)

        // 배경을 투명하게 설정
        bottomSheetDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // BottomSheetBehavior를 통해 슬라이드 가능하도록 설정
        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet!!)

        // BottomSheetDialog 표시
        bottomSheetDialog.show()

        // GestureDetector 설정
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                // 아래에서 위로 스크롤하는 경우
                if (e1 != null && e2.y < e1.y) {
                    // BottomSheet를 위로 움직이는 애니메이션
                    bottomSheet.animate()
                        .translationY(-bottomSheet.height.toFloat())
                        .setDuration(300)
                        .withEndAction {
                            // 애니메이션이 끝난 후 DetailActivity로 이동
                            val intent = Intent(this@NearActivity, DetailPageActivity::class.java)
                            startActivity(intent)
                            bottomSheetDialog.dismiss()  // DetailActivity로 이동 시 다이얼로그 닫기
                        }
                    return true
                }
                return false
            }
        })

        // BottomSheet 터치 이벤트 처리
        bottomSheet.setOnTouchListener { v, event ->
            // GestureDetector 이벤트 처리
            gestureDetector.onTouchEvent(event)
            false
        }

        // 더보기 버튼 클릭 시 DetailActivity 실행
        val moreButton: TextView = bottomSheetView.findViewById(R.id.more_button)
        moreButton.setOnClickListener {
            val intent = Intent(this@NearActivity, DetailPageActivity::class.java)
            startActivity(intent)
            bottomSheetDialog.dismiss()  // DetailActivity로 이동 시 다이얼로그 닫기
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            //TODO:인증되기 전 로딩 화면 띄우기 (다른 XML 파일)
        } else {
            initializeMap()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                initializeMap()
            } else {
                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initializeMap() {
        mapView = findViewById(R.id.map_view)

        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                Log.d("NearActivity", "MapView destroyed")
            }

            override fun onMapError(error: Exception) {
                Log.e("NearActivity", "Map error: ${error.message}")
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(map: KakaoMap) {
                kakaoMap = map
                Log.d("NearActivity", "KakaoMap is ready")

                // 현재 위치를 중심으로 지도를 이동
                setMapToCurrentLocation()
            }
        })
    }

    private fun setMapToCurrentLocation() {
        val sharedPreferences = getSharedPreferences("LocationCache", MODE_PRIVATE)

        // 이전에 저장된 위치 가져오기 (위도, 경도)
        val cachedLatitude = sharedPreferences.getString("latitude", null)?.toDoubleOrNull()
        val cachedLongitude = sharedPreferences.getString("longitude", null)?.toDoubleOrNull()

        var cachedPosition: LatLng? = null
        if (cachedLatitude != null && cachedLongitude != null) {
            cachedPosition = LatLng.from(cachedLatitude, cachedLongitude)
            kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(cachedPosition, 16))
            addMarkerToMap(cachedPosition, null)
        }

        // 위치 권한이 허용되었는지 확인
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { location ->
                    location?.let {
                        val currentPosition = LatLng.from(it.latitude, it.longitude)

                        // 이전 위치와 현재 위치 비교, 거리 차가 10미터 이상인 경우만
                        //초기값이 null인 경우는 바로 true
                        val isLocationChanged = cachedPosition?.let { cachedPos ->
                            val cachedLocation = Location("").apply {
                                latitude = cachedPos.latitude
                                longitude = cachedPos.longitude
                            }
                            val currentLocation = Location("").apply {
                                latitude = it.latitude
                                longitude = it.longitude
                            }
                            cachedLocation.distanceTo(currentLocation) > 10 // 10 meters threshold
                        } ?: true

                        if (isLocationChanged) {
                            // 현재 위치로 지도 중심 설정
                            kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(currentPosition, 16))
                            addMarkerToMap(currentPosition, null)

                            // 새로운 위치를 캐싱
                            val editor = sharedPreferences.edit()
                            editor.putString("latitude", it.latitude.toString())
                            editor.putString("longitude", it.longitude.toString())
                            editor.apply()
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun setToiletLabel() {
        // Firebase에서 화장실 데이터를 가져옴
        ToiletData.getToiletAllData(
            onSuccess = { toilets ->
                if (toilets.isNotEmpty()) {
                    toilets.forEach { toilet ->
                        try {
                            // 로그로 화장실 데이터 출력
                            Log.d("$Tag Firebase", "Toilet data: $toilet")

                            // 위도와 경도를 Double로 변환
                            val latitude = toilet.wgs84_latitude.toString().toDoubleOrNull()
                            val longitude = toilet.wgs84_longitude.toString().toDoubleOrNull()

                            // 유효한 위도와 경도인지 확인
                            if (latitude != null && longitude != null) {
                                val toiletLatLng = LatLng.from(latitude, longitude)
                                addMarkerToMap(toiletLatLng, toilet)
                            } else {
                                Log.e("$Tag setToiletLabel", "Invalid latitude or longitude for toilet: ${toilet.restroom_name}")
                            }
                        } catch (e: Exception) {
                            // 예외 발생 시 오류 로그 출력
                            Log.e("$Tag FirebaseError", "Error processing toilet data: ${e.message}", e)
                        }
                    }
                } else {
                    Log.e("$Tag setToiletLabel", "No toilet data found.")
                }
            },
            onFailure = { exception ->
                Log.e("$Tag FirebaseError", "Failed to load toilet data from Firebase: ${exception.message}", exception)
            }
        )
    }





    private fun addMarkerToMap(position: LatLng, toilet: ToiletModel?) {
        setToiletLabel()

        val iconRes = if (toilet == null) {
            // 현재 위치인 경우 star_icon을 사용
            R.drawable.logo
        } else {
            // 화장실 위치인 경우 logo를 사용
            R.drawable.star_main
        }

        // 1. LabelStyles 생성하기 - 위치에 따라 다른 아이콘을 설정
        val styles = kakaoMap.labelManager
            ?.addLabelStyles(LabelStyles.from(LabelStyle.from(iconRes))
            )

        // 2. LabelOptions 생성하기
        val options = LabelOptions.from(position)
            .setStyles(styles)
            .setClickable(true)

        // 3. LabelLayer 가져오기 (또는 커스텀 Layer 생성)
        val layer = kakaoMap.labelManager?.layer

        // 4. LabelLayer에 LabelOptions을 넣어 Label 생성하기
        val label = layer?.addLabel(options)

        // 5. Label 클릭 이벤트 처리
        kakaoMap.setOnLabelClickListener { kakaoMap, layer, clickedLabel ->
            if (clickedLabel == label) {
                initializeBottomSheet()
                true
            } else {
                false  // 다른 이벤트 리스너로 이벤트 전달
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()
    }

    override fun onPause() {
        super.onPause()
        mapView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

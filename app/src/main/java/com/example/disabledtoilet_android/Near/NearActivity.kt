package com.example.disabledtoilet_android.Near

import ToiletModel
import android.Manifest
import android.content.ContentValues.TAG
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
import com.example.disabledtoilet_android.NonloginActivity
import com.example.disabledtoilet_android.NonloginActivity.Companion
import com.example.disabledtoilet_android.R
import com.example.disabledtoilet_android.ToiletSearch.ToiletData
import com.example.disabledtoilet_android.Utility.Dialog.LoadingDialog
import com.example.disabledtoilet_android.databinding.ActivityNearBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kakao.vectormap.*
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.LatLng
import com.google.firebase.FirebaseApp
import com.kakao.vectormap.camera.CameraPosition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NearActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var kakaoMap: KakaoMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityNearBinding

    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private val Tag = "NearActivity"

    val loadingDialog = LoadingDialog()

    // 화장실 데이터를 저장할 리스트
    private val toiletList = mutableListOf<ToiletModel>()

    // 활성화된 마커 관리
    private val activeMarkers = mutableListOf<ToiletModel>()

    private val labelToToiletMap = mutableMapOf<Label, ToiletModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNearBinding.inflate(layoutInflater)



        KakaoMapSdk.init(this, "ce27585c8cc7c468ac7c46901d87199d")
        setContentView(R.layout.activity_near)

        FirebaseApp.initializeApp(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // MapView 초기화
        mapView = findViewById(R.id.map_view)
        initializeMapView()  // 새로운 함수로 맵 초기화 로직 분리

        // 버튼 설정
        val backToCurBtn : ImageButton = findViewById(R.id.map_return_cur_pos_btn)
        backToCurBtn.setOnClickListener {
            if (::kakaoMap.isInitialized) {
                moveCameraToCachedLocation()
            }
        }

        val backBtn : ImageButton = findViewById(R.id.back_button)
        backBtn.setOnClickListener {
            onBackPressed()
        }
    }
    private fun initializeMapView() {
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                Log.d(Tag, "MapView destroyed")
            }

            override fun onMapError(error: Exception) {
                Log.e(Tag, "Map error: ${error.message}")
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(map: KakaoMap) {
                kakaoMap = map
                Log.d(Tag, "KakaoMap is ready")

                // 맵이 준비되면 클릭 리스너 설정
                setupMapClickListener()

                // 위치 권한 확인 및 현재 위치 설정
                checkLocationPermission()
            }
        })
    }
    private fun setupMapClickListener() {
        kakaoMap.setOnLabelClickListener { _, _, clickedLabel ->
            Log.d("BottomSheet", "Label clicked: $clickedLabel")
            Log.d("BottomSheet", "Map size: ${labelToToiletMap.size}")
            val toilet = labelToToiletMap[clickedLabel]
            Log.d("BottomSheet", "Found toilet: $toilet")

            if (toilet != null) {
                Log.d("BottomSheet", "Matched label clicked. Showing Bottomsheet for toilet: ${toilet.restroom_name}")
                initializeBottomSheet(toilet)
                true
            } else {
                Log.d("BottomSheet", "Unmatched label clicked")
                false
            }
        }
    }


    private fun initializeBottomSheet(toilet: ToiletModel) {
        Log.d("BottomSheet", "Initializing BottomSheet for toilet: ${toilet.restroom_name}")
        val bottomSheetView = layoutInflater.inflate(R.layout.detail_bottomsheet, null)
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        bottomSheetDialog.setContentView(bottomSheetView)

        bottomSheetView.findViewById<TextView>(R.id.toilet_name).text = toilet.restroom_name
        bottomSheetView.findViewById<TextView>(R.id.toilet_address).text = if (toilet.address_road.isNullOrBlank() ||
                toilet.address_road == "\"" ||
                toilet.address_road == "\"\"" ||
                toilet.address_road == "") {
            "정보 없음"
        } else {
            toilet.address_road
        }

        bottomSheetView.findViewById<TextView>(R.id.toilet_opening_hours).text = if (toilet.opening_hours.isNullOrBlank() ||
            toilet.opening_hours == "\"" ||
            toilet.opening_hours == "\"\"" ||
            toilet.opening_hours == "") {
            "정보 없음"
        } else {
            toilet.opening_hours
        }

        bottomSheetView.findViewById<TextView>(R.id.toilet_distance).text = run {
            val sharedPreferences = getSharedPreferences("LocationCache", MODE_PRIVATE)
            val currentLatitude = sharedPreferences.getString("latitude", null)?.toDoubleOrNull()
            val currentLongitude = sharedPreferences.getString("longitude", null)?.toDoubleOrNull()

            if (currentLatitude != null && currentLongitude != null) {
                // 현재 위치의 Location 객체 생성
                val currentLocation = Location("").apply {
                    latitude = currentLatitude
                    longitude = currentLongitude
                }

                // 화장실 위치의 Location 객체 생성
                val toiletLocation = Location("").apply {
                    latitude = toilet.wgs84_latitude
                    longitude = toilet.wgs84_longitude
                }

                // 두 위치 사이의 거리 계산 (미터 단위)
                val distanceInMeters = currentLocation.distanceTo(toiletLocation)

                // 거리를 적절한 형식으로 변환
                when {
                    distanceInMeters < 1000 -> "${distanceInMeters.toInt()}m"
                    else -> String.format("%.1fkm", distanceInMeters / 1000)
                }
            } else {
                "-"
            }
        }
        bottomSheetDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
//        bottomSheet?.let { sheet ->
//            val behavior = BottomSheetBehavior.from(sheet)
//
//            val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
//                override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
//                    if (e1 != null && e2.y < e1.y) {
//                        sheet.animate()
//                            .translationY(-sheet.height.toFloat())
//                            .setDuration(300)
//                            .withEndAction {
//                                val intent = Intent(this@NearActivity, DetailPageActivity::class.java)
//                                intent.putExtra("TOILET_DATA", toilet)
//                                startActivity(intent)
//                                bottomSheetDialog.dismiss()
//                            }
//                        return true
//                    }
//                    return false
//                }
//            })
//
//            sheet.setOnTouchListener { _, event ->
//                gestureDetector.onTouchEvent(event)
//                false
//            }
//        }

        bottomSheetDialog.show()

        val moreButton: TextView = bottomSheetView.findViewById(R.id.more_button)
        moreButton.setOnClickListener {
            val intent = Intent(this@NearActivity, DetailPageActivity::class.java)
            intent.putExtra("TOILET_DATA", toilet)
            startActivity(intent)
            bottomSheetDialog.dismiss()
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // 현재 위치를 중심으로 지도를 이동
            CoroutineScope(Dispatchers.Main).launch {
                loadingDialog.show(supportFragmentManager, loadingDialog.tag)
                setMapToCurrentLocation { success ->
                    loadingDialog.dismiss()
                    if (success) {
                        fetchToiletDataAndDisplay()
                    }
                }
            }
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
                Log.d(Tag, "MapView destroyed")
            }

            override fun onMapError(error: Exception) {
                Log.e(Tag, "Map error: ${error.message}")
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(map: KakaoMap) {
                kakaoMap = map
                Log.d(Tag, "KakaoMap is ready")

                // 현재 위치를 중심으로 지도를 이동
                CoroutineScope(Dispatchers.Main).launch {
                    loadingDialog.show(supportFragmentManager, loadingDialog.tag)
                    setMapToCurrentLocation { success ->
                        // 데이터 로드 완료 후 loadingDialog.dismiss() 호출
                        loadingDialog.dismiss()

                        if (success) {
                            Log.d(TAG, "Toilet data loaded successfully.")
                            // 추가적인 성공 처리 로직을 여기에 작성할 수 있습니다.
                        } else {
                            Log.e(TAG, "Failed to load toilet data.")
                            // 실패 처리 로직을 여기에 작성할 수 있습니다.
                        }
                    }
                }
                // 화장실 데이터 가져오기 및 표시
                fetchToiletDataAndDisplay()
            }
        })

        // 라벨 클릭 리스너 설정
        kakaoMap.setOnLabelClickListener { _, _, clickedLabel ->
            Log.d("BottomSheet", "Label clicked: $clickedLabel")
            Log.d("BottomSheet", "Map size: ${labelToToiletMap.size}")
            val toilet = labelToToiletMap[clickedLabel]
            Log.d("BottomSheet", "Found toilet: $toilet")

            if (toilet != null) {
                Log.d("BottomSheet", "Matched label clicked. Showing Bottomsheet for toilet: ${toilet.restroom_name}")
                initializeBottomSheet(toilet)
                true
            } else {
                Log.d("BottomSheet", "Unmatched label clicked")
                false
            }
        }
    }

    private fun setMapToCurrentLocation(onComplete: (Boolean) -> Unit) {
        val sharedPreferences = getSharedPreferences("LocationCache", MODE_PRIVATE)

        // 이전에 저장된 위치 가져오기 (위도, 경도)
        val cachedLatitude = sharedPreferences.getString("latitude", null)?.toDoubleOrNull()
        val cachedLongitude = sharedPreferences.getString("longitude", null)?.toDoubleOrNull()

        var cachedPosition: LatLng? = null
        if (cachedLatitude != null && cachedLongitude != null) {
            cachedPosition = LatLng.from(cachedLatitude, cachedLongitude)
            kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(cachedPosition, 16))
            addMarkerToMapCur(cachedPosition)
        }

        // 위치 권한이 허용되었는지 확인
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val currentPosition = LatLng.from(location.latitude, location.longitude)

                        // 이전 위치와 현재 위치 비교, 거리 차가 10미터 이상인 경우만
                        val isLocationChanged = cachedPosition?.let { cachedPos ->
                            val cachedLocation = Location("").apply {
                                latitude = cachedPos.latitude
                                longitude = cachedPos.longitude
                            }
                            val currentLocation = Location("").apply {
                                latitude = location.latitude
                                longitude = location.longitude
                            }
                            cachedLocation.distanceTo(currentLocation) > 10 // 10 meters threshold
                        } ?: true

                        if (isLocationChanged) {
                            // 현재 위치로 지도 중심 설정
                            kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(currentPosition, 16))
                            addMarkerToMapCur(currentPosition)

                            // 새로운 위치를 캐싱
                            val editor = sharedPreferences.edit()
                            editor.putString("latitude", location.latitude.toString())
                            editor.putString("longitude", location.longitude.toString())
                            editor.apply()
                        }
                        onComplete(true)  // 위치 설정 성공
                    } else {
                        onComplete(false)  // 위치를 가져오지 못함
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    onComplete(false)  // 위치 설정 실패
                }
        } else {
            onComplete(false)  // 위치 권한이 없음
        }
    }


    /**
     * Firebase에서 데이터를 가져와 리스트에 저장하고 현재 지도 범위에 맞는 마커를 표시
     */
    private fun fetchToiletDataAndDisplay() {
        val toilets = ToiletData.getToiletAllData()
        if (toilets!!.isNotEmpty()) {
            toilets.forEach{
                toilet ->
                val pos = LatLng.from(toilet.wgs84_latitude, toilet.wgs84_longitude)
                if(toilet.wgs84_latitude != 0.0 && toilet.wgs84_longitude != 0.0){
                    addMarkerToMapToilet(pos, toilet)
                }
            }
        } else {
            Log.e(TAG, "No toilet data found in ToiletRepository.")
        }
    }

    /**
     * 현재 지도 화면에 보이는 영역 내의 화장실을 필터링하여 표시
     */
//    private fun displayToiletsWithinView() {
//        if (!::kakaoMap.isInitialized) return
//
//        // 지도의 현재 화면 영역 가져오기
//        val visibleRegion = kakaoMap.cameraPosition?.let { cameraPosition ->
//            val center = cameraPosition.position // 현재 카메라의 중심 좌표
//            val zoomLevel = cameraPosition.zoomLevel // 현재 줌 레벨
//
//            // 카메라의 중심을 기준으로 화면에 보이는 영역을 계산합니다.
//            // 이 부분은 Kakao Map API에서 제공하는 기능에 따라 조정해야 합니다.
//
//            // 예를 들어, 특정 거리만큼의 범위를 계산하여 visibleRegion을 생성할 수 있습니다.
//            // 여기서는 단순히 중심 좌표를 visibleRegion으로 반환하는 예시입니다.
//            // 실제 visibleRegion 객체를 구성하는 방법은 Kakao Map의 API에 따라 다를 수 있습니다.
//
//            // 가상의 visibleRegion 객체 생성 (실제 API에 맞춰 조정 필요)
//            VisibleRegion(
//                southwest = LatLng.from(center.latitude - 0.01, center.longitude - 0.01),
//                northeast = LatLng.from(center.latitude + 0.01, center.longitude + 0.01)
//            )
//        }
//
//        val bounds = visibleRegion!!.latLngBounds
//
//        // 현재 화면에 보이는 화장실 데이터 필터링
//        val visibleToilets = ToiletData.getToiletsWithinBounds(
//            bounds.southWest.latitude,
//            bounds.southWest.longitude,
//            bounds.northEast.latitude,
//            bounds.northEast.longitude
//        )
//
//        Log.d(TAG, "Visible toilets count: ${visibleToilets.size}")
//
//        val markersToKeep: MutableSet<ToiletModel> = mutableSetOf() // Toilet 타입을 명시적으로 선언
//
//        visibleToilets.forEach { toilet ->
//            markersToKeep.add(toilet)
//            if (!activeMarkers.contains(toilet)) {
//                val position = LatLng.from(toilet.wgs84_latitude, toilet.wgs84_longitude)
//                val label = addMarkerToMapToilet(position)
//
//            }
//        }
//
//        // 현재 보이지 않는 마커는 제거
//        val iterator = activeMarkers.iterator()
//        while (iterator.hasNext()) {
//            val toilet = iterator.next()
//            if (!markersToKeep.contains(toilet)) { // entry.key가 Toilet 타입이어야 함
//                // 레이블 제거를 위한 올바른 메서드를 사용해야 합니다.
//                kakaoMap.labelManager?.layer?.let { layer ->
//                    // 예: layer.remove(entry.value) 와 같은 올바른 메서드 사용
//                }
//                iterator.remove()
//            }
//        }
//    }

    // 마커 추가 함수 수정: ToiletModel에서 ToiletData로 변경
    private fun addMarkerToMapToilet(position: LatLng, toilet: ToiletModel) {
        // 줌 레벨에 따른 라벨 스타일 정의
        val styles = kakaoMap.labelManager?.addLabelStyles(
            LabelStyles.from(
                LabelStyle.from(R.drawable.map_pin1).setZoomLevel(10),
                LabelStyle.from(R.drawable.map_pin2).setZoomLevel(13),
                LabelStyle.from(R.drawable.map_pin3).setZoomLevel(16),
                LabelStyle.from(R.drawable.map_pin4).setZoomLevel(19)
            )
        )

        // LabelOptions 생성하기
        val options = LabelOptions.from(position)
            .setStyles(styles)
            .setClickable(true)

        // LabelLayer에 LabelOptions을 넣어 Label 생성하기
        val layer = kakaoMap.labelManager?.layer
        val label = layer?.addLabel(options)

        // 생성된 라벨과 화장실 정보를 맵에 저장
        if (label != null) {
            labelToToiletMap[label] = toilet
            Log.d("BottomSheet", "Added to map - Label: $label, Toilet: ${toilet.restroom_name}")
        }
    }


    private fun addMarkerToMapCur(position: LatLng): Label? {
        val styles = kakaoMap.labelManager?.addLabelStyles(
            LabelStyles.from(
                LabelStyle.from(R.drawable.cur2)
            )
        )
        // LabelOptions 생성하기
        val options = LabelOptions.from(position)
            .setStyles(styles)
            .setClickable(true)

        // LabelLayer에 LabelOptions을 넣어 Label 생성하기
        val layer = kakaoMap.labelManager?.layer
        val label = layer?.addLabel(options)

        return label
    }

    // 새로운 함수 추가: 버튼 클릭 시 캐시된 위치로 이동
    private fun moveCameraToCachedLocation() {
        // SharedPreferences에서 캐시된 위치 정보 불러오기
        val sharedPreferences = getSharedPreferences("LocationCache", MODE_PRIVATE)
        val cachedLatitude = sharedPreferences.getString("latitude", null)?.toDoubleOrNull()
        val cachedLongitude = sharedPreferences.getString("longitude", null)?.toDoubleOrNull()

        if (cachedLatitude != null && cachedLongitude != null) {
            val cachedPosition = LatLng.from(cachedLatitude, cachedLongitude)
            // 지도의 중심을 캐시된 위치로 이동
            kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(cachedPosition, 16))
            // 캐시된 위치에 마커 추가 (현재 위치)
            addMarkerToMapCur(cachedPosition)
            Toast.makeText(this, "캐시된 현재 위치로 이동합니다.", Toast.LENGTH_SHORT).show()
        } else {
            // 캐시된 위치 정보가 없는 경우 사용자에게 알림
            Toast.makeText(this, "캐시된 현재 위치가 없습니다.", Toast.LENGTH_SHORT).show()
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

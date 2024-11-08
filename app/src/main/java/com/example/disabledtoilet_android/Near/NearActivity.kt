package com.example.disabledtoilet_android.Near

import ToiletModel
import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.disabledtoilet_android.Detail.DetailPageActivity
import com.example.disabledtoilet_android.R
import com.example.disabledtoilet_android.ToiletSearch.ToiletData
import com.example.disabledtoilet_android.Utility.Dialog.LoadingDialog
import com.example.disabledtoilet_android.databinding.ActivityNearBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kakao.vectormap.*
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.LatLng
import com.google.firebase.FirebaseApp
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.Serializable
import java.net.URLEncoder

class NearActivity : AppCompatActivity() {

    private var searchingToilet: ToiletModel? = null
    private lateinit var mapView: MapView
    private lateinit var kakaoMap: KakaoMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityNearBinding

    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private val Tag = "NearActivity"

    val loadingDialog = LoadingDialog()

    private val labelToToiletMap = mutableMapOf<Label, ToiletModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNearBinding.inflate(layoutInflater)

        KakaoMapSdk.init(this, "ce27585c8cc7c468ac7c46901d87199d")

        binding = ActivityNearBinding.inflate(layoutInflater)

        setContentView(R.layout.activity_near)

        FirebaseApp.initializeApp(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // MapView 초기화
        mapView = findViewById(R.id.map_view)

        // MapView 초기화 - 코루틴을 사용하여 비동기 처리
        CoroutineScope(Dispatchers.Main).launch {
            mapView = findViewById(R.id.map_view)
            initializeMapView()
        }

        // 인텐트 지점 찾기
        val rootActivity = intent.getStringExtra("rootActivity")


        // 버튼 설정
        val backToCurBtn : ImageButton = findViewById(R.id.map_return_cur_pos_btn)
        backToCurBtn.setOnClickListener {
            moveCameraToCachedLocation()
        }

        val backBtn : ImageButton = findViewById(R.id.back_button)
        backBtn.setOnClickListener {
            onBackPressed()
        }



        when(rootActivity){
            null -> {
                Log.d("test log", "root activity data is null")
            }

            "ToiletFilterSearchActivity" -> {
                val parcelableData = intent.getParcelableExtra<ToiletModel>("toiletData")
                if (parcelableData is ToiletModel) {
                    searchingToilet = parcelableData
                    Log.d("test log", "Restroom Name: ${searchingToilet!!.restroom_name}")
                    if (searchingToilet != null){
                        moveCameraToToilet(searchingToilet!!)
                    }
                    initializeBottomSheet(searchingToilet!!)
                } else {
                    Log.e("test log", "parcelable data type is not matched")
                }
            }
        }
    }
    private fun initializeMapView() {
        CoroutineScope(Dispatchers.Main).launch {
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


    @SuppressLint("ClickableViewAccessibility")
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
            "-"
        } else {
            toilet.address_road
        }

        bottomSheetView.findViewById<TextView>(R.id.toilet_opening_hours).text = if (toilet.opening_hours.isNullOrBlank() ||
            toilet.opening_hours == "\"" ||
            toilet.opening_hours == "\"\"" ||
            toilet.opening_hours == "") {
            "-"
        } else {
            toilet.opening_hours
        }

        bottomSheetView.findViewById<TextView>(R.id.toilet_distance).text = run {
            val sharedPreferences = getSharedPreferences("LocationCache", MODE_PRIVATE)
            val currentLatitude = sharedPreferences.getString("latitude", null)?.toDoubleOrNull()
            val currentLongitude = sharedPreferences.getString("longitude", null)?.toDoubleOrNull()

            if (currentLatitude != null && currentLongitude != null) {
                val currentLocation = Location("").apply {
                    latitude = currentLatitude
                    longitude = currentLongitude
                }

                val toiletLocation = Location("").apply {
                    latitude = toilet.wgs84_latitude
                    longitude = toilet.wgs84_longitude
                }

                val distanceInMeters = currentLocation.distanceTo(toiletLocation)

                when {
                    distanceInMeters < 1000 -> "${distanceInMeters.toInt()}m"
                    else -> String.format("%.1fkm", distanceInMeters / 1000)
                }
            } else {
                "-"
            }
        }
        bottomSheetDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        // 주석 처리된 제스처 관련 코드는 그대로 유지

        bottomSheetDialog.show()

        val moreButton: TextView = bottomSheetView.findViewById(R.id.more_button)
        moreButton.setOnClickListener {
            val intent = Intent(this@NearActivity, DetailPageActivity::class.java)
            intent.putExtra("TOILET_DATA", toilet)
            startActivity(intent)
            bottomSheetDialog.dismiss()
        }

        // 네비게이션 연동
        val navBtn: LinearLayout = bottomSheetView.findViewById(R.id.toilet_navigation_btn)
        navBtn.setOnClickListener {
            Log.d("navBtn", "Navigation button clicked")
            showKakaoMap(toilet)
        }

        val shareBtn : LinearLayout = bottomSheetView.findViewById(R.id.share_btn)
        shareBtn.setOnClickListener {
            Log.d("shareBtn", "share button clicked")
            shareKakaoMap(toilet)
        }


    }

    private fun shareKakaoMap(toilet: ToiletModel) {
        val toiletAddress = toilet.address_road ?: ""
        val toiletLatitude = toilet.wgs84_latitude
        val toiletLongitude = toilet.wgs84_longitude

        // 웹에서 열 수 있는 카카오맵 URL
        val kakaoMapWebUrl = "https://map.kakao.com/link/map/${toiletLatitude},${toiletLongitude}"

        // 모바일에서 카카오맵 앱으로 열 수 있는 URL
        val kakaoMapAppUrl = "kakaomap://look?p=${toiletLatitude},${toiletLongitude}"

        // 길찾기를 위한 웹 URL (카카오맵 웹에서 길찾기 기능 사용)
        val kakaoMapRouteWebUrl = "https://map.kakao.com/link/to/${toiletAddress},${toiletLatitude},${toiletLongitude}"

        val defaultFeed = FeedTemplate(
            content = Content(
                title = "방광곡곡 - 화장실 위치",
                description = toiletAddress,
                imageUrl = "https://mud-kage.kakao.com/dn/Q2iNx/btqgeRgV54P/VLdBs9cvyn8BJXB3o7N8UK/kakaolink40_original.png",
                link = Link(
                    webUrl = kakaoMapWebUrl,
                    mobileWebUrl = kakaoMapWebUrl
                )
            ),
            buttons = listOf(
                Button(
                    "위치 보기",
                    Link(
                        webUrl = kakaoMapWebUrl,
                        mobileWebUrl = kakaoMapAppUrl,
                        androidExecutionParams = mapOf(
                            "key1" to "value1",
                            "key2" to "value2"
                        )
                    )
                ),
                Button(
                    "길찾기",
                    Link(
                        webUrl = kakaoMapRouteWebUrl,
                        mobileWebUrl = kakaoMapRouteWebUrl
                    )
                )
            )
        )

        if (ShareClient.instance.isKakaoTalkSharingAvailable(this)) {
            // 카카오톡으로 카카오톡 공유 가능
            ShareClient.instance.shareDefault(this, defaultFeed) { sharingResult, error ->
                if (error != null) {
                    Log.e("KakaoShare", "카카오톡 공유 실패", error)
                    Toast.makeText(this, "카카오톡 공유에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
                else if (sharingResult != null) {
                    Log.d("KakaoShare", "카카오톡 공유 성공")
                    startActivity(sharingResult.intent)

                    // 버튼 클릭 이벤트를 처리하기 위한 리스너 설정
                    sharingResult.intent.putExtra("EXTRA_BUTTON_CLICK_LISTENER", object : ButtonClickListener {
                        override fun onButtonClick(buttonType: String) {
                            when (buttonType) {
                                "위치 보기" -> {
                                    Log.d("KakaoShare", "위치 보기 버튼이 클릭되었습니다.")
                                    openKakaoMap(kakaoMapAppUrl, kakaoMapWebUrl)
                                }
                                "길찾기" -> {
                                    Log.d("KakaoShare", "길찾기 버튼이 클릭되었습니다.")
                                    openKakaoMap(kakaoMapRouteWebUrl, kakaoMapRouteWebUrl)
                                }
                            }
                        }
                    })
                }
            }
        } else {
            // 카카오톡 미설치: 웹 공유 사용
            val sharerUrl = WebSharerClient.instance.makeDefaultUrl(defaultFeed)

            try {
                KakaoCustomTabsClient.openWithDefault(this, sharerUrl)
            } catch (e: UnsupportedOperationException) {
                // CustomTabes를 지원하지 않는 환경에서는 인터넷 브라우저 인텐트로 공유 웹페이지 열기
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl.toString()))
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "공유에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 버튼 클릭 이벤트를 처리하기 위한 인터페이스
    interface ButtonClickListener : Serializable {
        fun onButtonClick(buttonType: String)
    }

    // 카카오맵 열기 함수
    private fun openKakaoMap(appUrl: String, webUrl: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(appUrl))
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            startActivity(intent)
        } catch (e: Exception) {
            // 카카오맵 앱이 설치되어 있지 않은 경우 웹 URL로 열기
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
            startActivity(webIntent)
        }
    }


    // drawable 리소스를 URI 문자열로 변환하는 함수
    private fun getDrawableUriString(drawableId: Int): String {
        val drawable = ContextCompat.getDrawable(this, drawableId)
        val bitmap = (drawable as BitmapDrawable).bitmap

        val file = File(cacheDir, "temp_image.png")
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()

        return Uri.fromFile(file).toString()
    }



    private fun showKakaoMap(toilet: ToiletModel) {
        // 현재 위치 가져오기 (이전에 저장된 위치 사용)
        val sharedPreferences = getSharedPreferences("LocationCache", MODE_PRIVATE)
        val currentLatitude = sharedPreferences.getString("latitude", null)?.toDoubleOrNull()
        val currentLongitude = sharedPreferences.getString("longitude", null)?.toDoubleOrNull()


        Log.d("showKakaoMap", "showKakaoMap 1")
        if (currentLatitude != null && currentLongitude != null) {
            // 카카오맵 URL Scheme 생성
            val uri = Uri.parse("kakaomap://route?" +
                    "sp=$currentLatitude,$currentLongitude" +  // 출발지 (현재 위치)
                    "&ep=${toilet.wgs84_latitude},${toilet.wgs84_longitude}" +  // 도착지 (화장실 위치)
                    "&by=FOOT")  // 이동 수단 (도보)

            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addCategory(Intent.CATEGORY_BROWSABLE)

            try {
                startActivity(intent)
            } catch (e: Exception) {
                // 카카오맵 앱이 설치되어 있지 않은 경우
                Toast.makeText(this, "카카오맵 앱이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()

                // 카카오맵 앱 설치 페이지로 이동
                val playStoreUri = Uri.parse("market://details?id=net.daum.android.map")
                val playStoreIntent = Intent(Intent.ACTION_VIEW, playStoreUri)

                try {
                    startActivity(playStoreIntent)
                } catch (e: Exception) {
                    // 플레이 스토어를 열 수 없는 경우 웹 브라우저로 열기
                    val webIntent = Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=net.daum.android.map"))
                    startActivity(webIntent)
                }
            }
        } else {
            Toast.makeText(this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
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

    // 장소 검색에서 내 주변으로 넘어오면 화장실 위치로 카메라 옮김
    private fun moveCameraToToilet(toiletData: ToiletModel){
        val latitude = toiletData.wgs84_latitude
        val longitude = toiletData.wgs84_longitude

        // 데이터중에 값이 0인 애들이 많음
        if (latitude.toInt() != 0 && longitude.toInt() != 0) {
            val toiletPosition = LatLng.from(latitude, longitude)
            // 지도의 중심을 화장실 위치로 이동
            if (::kakaoMap.isInitialized){
                // 여기 비동기 처리하면 바꿔줄 것
                kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(toiletPosition, 16))
            }
        } else {
            // 화장실 위치 정보가 없는 경우 사용자에게 알림
            Toast.makeText(this, "위치데이터 준비 중 입니다.", Toast.LENGTH_SHORT).show()
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

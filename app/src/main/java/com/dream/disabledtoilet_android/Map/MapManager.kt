package com.dream.disabledtoilet_android.Map

import ToiletModel
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.dream.disabledtoilet_android.Near.UILayer.NearActivity
import com.dream.disabledtoilet_android.R
import com.dream.disabledtoilet_android.ToiletSearch.ToiletData
import com.dream.disabledtoilet_android.Utility.Dialog.utils.LocationHelper
import com.kakao.vectormap.*
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapManager(private val context: Context) {

    private lateinit var mapView: MapView
    private lateinit var kakaoMap: KakaoMap
    private val labelToToiletMap = mutableMapOf<Label, ToiletModel>()
    private var isFirst = true

    // 이전에 클릭된 화장실을 추적하기 위한 변수
    private var lastClickedToilet: Label? = null
    private val locationHelper by lazy { LocationHelper(context) }


    // 지도 초기화 함수
    suspend fun initializeMapView(): Boolean {
        val isSuccess = CompletableDeferred<Boolean>()

        withContext(Dispatchers.Main) { // UI 스레드에서 실행
            mapView = (context as NearActivity).findViewById(R.id.map_view)
            mapView.start(object : MapLifeCycleCallback() {
                override fun onMapDestroy() {
                    Log.d("MapManager", "MapView destroyed")
                }

                override fun onMapError(error: Exception) {
                    Log.e("MapManager", "Map error: ${error.message}")
                    isSuccess.completeExceptionally(error)
                }
            }, object : KakaoMapReadyCallback() {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onMapReady(map: KakaoMap) {
                    kakaoMap = map
                    Log.d("MapManager", "KakaoMap is ready")
                    setupMapClickListener()
                    isSuccess.complete(true)
                }
            })
        }

        return isSuccess.await()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    /**
     * ToiletRepository의 finalResult를 기반으로 마커를 추가하는 함수
     */
    suspend fun fetchAndDisplayFilteredToilets(filteredToilets : MutableList<ToiletModel>): Boolean {
        val isSuccess = CompletableDeferred<Boolean>()

        Log.d("MapManager 00", filteredToilets.toString())

        withContext(Dispatchers.IO) {

            withContext(Dispatchers.Main) {

                Log.d("MapManager 01 " , filteredToilets.toString())
                if (filteredToilets.isNotEmpty()) {
                    Log.d("MapManager 1", isFirst.toString())

                    // 새 레이블 추가
                    if(isFirst){
                        Log.d("MapManager 2", isFirst.toString())
                        filteredToilets.forEach { toilet ->
                            val pos = LatLng.from(toilet.wgs84_latitude, toilet.wgs84_longitude)
                            if (toilet.wgs84_latitude != 0.0 && toilet.wgs84_longitude != 0.0) {
                                addMarkerToMapToilet(pos, toilet)

                            }

                        }
                        isFirst = false
                    }

                    else{
                        // 필터된 화장실에 속하는 레이블은 labelsToShow에, 아니면 labelsToRemove에 추가
                        val labelsToShow = labelToToiletMap.filter { (_, toilet) ->
                            toilet in filteredToilets
                        }.keys

                        val labelsToRemove = labelToToiletMap.filter { (_, toilet) ->
                            toilet !in filteredToilets
                        }.keys

                        Log.d("MapManager", labelsToShow.size.toString())
                        Log.d("MapManager", labelsToRemove.size.toString())


                        labelsToShow.forEach { label ->
                            label.show()
                        }

                        labelsToRemove.forEach{ label ->
                            label.hide()
                        }
                    }
                    isSuccess.complete(true)
                } else {
                    Log.e("MapManager", "No filtered toilet data found.")
                    isSuccess.complete(false)
                }
            }
        }

        return isSuccess.await()
    }


    /**
     * 지도 클릭 리스너 설정 함수
     */

    @RequiresApi(Build.VERSION_CODES.O)
    fun setupMapClickListener() {
        kakaoMap.setOnLabelClickListener { _, _, clickedLabel ->
            val toilet = labelToToiletMap[clickedLabel]
            if (toilet != null) {
                // 이전에 클릭된 화장실의 레이블을 원래 상태로 복원
                lastClickedToilet?.let {
                    Log.d("지도 클릭", it.toString())
                    restoreLabelToOriginal(it)
                }

                // 클릭된 마커를 로고로 변경
                changeLabelToClicked(clickedLabel)

                //BottomSheet 초기화
                val activity = context as NearActivity
                true
            } else {
                false
            }
        }
    }

    // 클릭된 마커를 로고로 변경
    // 클릭된 마커를 로고로 변경
    private fun changeLabelToClicked(label: Label) {
        lastClickedToilet = label
        val currentZoomLevel = kakaoMap.cameraPosition?.zoomLevel

        // 현재 줌 레벨에 따라 아이콘을 설정
        val iconResId = when (currentZoomLevel) {
            10 -> R.drawable.aim_icon2
            13 -> R.drawable.aim_icon3
            16 -> R.drawable.aim_icon4
            else -> R.drawable.aim_icon4 // 기본값을 설정 (필요에 따라 변경)
        }

        val newStyles = kakaoMap.labelManager?.addLabelStyles(
            LabelStyles.from(
                LabelStyle.from(iconResId)
            )
        )

        // 스타일 변경 적용
        label.changeStyles(newStyles)
    }


    // 마커를 원래 스타일로 복원
    private fun restoreLabelToOriginal(label : Label) {
        val originalStyles = kakaoMap.labelManager?.addLabelStyles(
            LabelStyles.from(
                LabelStyle.from(R.drawable.map_pin1).setZoomLevel(10),
                LabelStyle.from(R.drawable.map_pin2).setZoomLevel(13),
                LabelStyle.from(R.drawable.map_pin3).setZoomLevel(16),
                LabelStyle.from(R.drawable.map_pin4).setZoomLevel(19)
            )
        )

        // 레이블이 클릭된 경우, 해당 레이블을 원래 스타일로 복원
        label.changeStyles(originalStyles)
        Log.d("MapManager", "Label restored to original")
    }

    // 화장실 위치로 카메라 이동 함수
    fun moveCameraToToilet(position : LatLng) {
        kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(position, 19), CameraAnimation.from(500, true, true))
        Log.d("test log", "화장실 위치로 이동")
    }


    // 캐시된 위치로 카메라 이동 함수
    fun moveCameraToCachedLocation() {
        val sharedPreferences = context.getSharedPreferences("LocationCache", Context.MODE_PRIVATE)
        val cachedLatitude = sharedPreferences.getString("latitude", null)?.toDoubleOrNull()
        val cachedLongitude = sharedPreferences.getString("longitude", null)?.toDoubleOrNull()

        Log.d("위치 moveCameraToCachedLocation" , sharedPreferences.toString())

        if (cachedLatitude != null && cachedLongitude != null) {
            val cachedPosition = LatLng.from(cachedLatitude, cachedLongitude)

            kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(cachedPosition, 16), CameraAnimation.from(500, true, true))
            addMarkerToMapCur(cachedPosition)
            Log.d("test log", "현재 위치로 이동")
            Toast.makeText(context, "현재 위치로 이동합니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "현재 위치를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 현재 위치 마커 추가 함수
    fun addMarkerToMapCur(position: LatLng, icon: String? = null): Label? {
        // styles 변수를 함수 시작 부분에서 선언
        val styles = if (icon == "search") {
            kakaoMap.labelManager?.addLabelStyles(
                LabelStyles.from(
                    LabelStyle.from(R.drawable.aim_icon3)
                )
            )
        } else {
            kakaoMap.labelManager?.addLabelStyles(
                LabelStyles.from(
                    LabelStyle.from(R.drawable.cur2)
                )
            )
        }

        val options = LabelOptions.from(position)
            .setStyles(styles)
            .setClickable(true)

        val layer = kakaoMap.labelManager?.layer
        return layer?.addLabel(options)
    }


    // 화장실 데이터 가져와 지도에 표시하는 함수
    suspend fun fetchAndDisplayToiletData() : Boolean {
        val toilets = ToiletData.getToiletAllData()
        val isSuccess = CompletableDeferred<Boolean>()

        withContext(Dispatchers.Main){
            if (toilets!!.isNotEmpty()) {
                toilets.forEach { toilet ->
                    val pos = LatLng.from(toilet.wgs84_latitude, toilet.wgs84_longitude)
                    if (toilet.wgs84_latitude != 0.0 && toilet.wgs84_longitude != 0.0) {
                        addMarkerToMapToilet(pos, toilet)
                    }
                }
                isSuccess.complete(true)
            } else {
                Log.e("MapManager", "No toilet data found in ToiletRepository.")
                isSuccess.complete(false)
            }
        }
        return isSuccess.await()
    }

    // 화장실 위치 마커 추가 함수
    private fun addMarkerToMapToilet(position: LatLng, toilet: ToiletModel) {
        val styles = kakaoMap.labelManager?.addLabelStyles(
            LabelStyles.from(
                LabelStyle.from(R.drawable.map_pin1).setZoomLevel(10),
                LabelStyle.from(R.drawable.map_pin2).setZoomLevel(13),
                LabelStyle.from(R.drawable.map_pin3).setZoomLevel(16),
                LabelStyle.from(R.drawable.map_pin4).setZoomLevel(19)
            )
        )
        val options = LabelOptions.from(position)
            .setStyles(styles)
            .setClickable(true)

        val layer = kakaoMap.labelManager?.layer
        val label = layer?.addLabel(options)

        if (label != null) {
            labelToToiletMap[label] = toilet
            Log.d("MapManager", "Added to map - Label: $label, Toilet: ${toilet.restroom_name}")
        }

    }

    // 카카오맵을 통해 화장실 위치를 보여주는 함수
    fun showKakaoMap(toilet: ToiletModel) {
        // 코루틴 스코프 내에서 실행
        CoroutineScope(Dispatchers.Main).launch {
            // 현재 위치 가져오기
            val currentLocation = locationHelper.getUserLocation()
            if (currentLocation != null) {
                // 출발지 (현재 위치)
                val startLatitude = currentLocation.latitude
                val startLongitude = currentLocation.longitude

                // 목적지 (화장실 위치)
                val toiletLatitude = toilet.wgs84_latitude
                val toiletLongitude = toilet.wgs84_longitude

                // 카카오맵 URI 생성
                val uri = Uri.parse(
                    "kakaomap://route?" +
                            "sp=$startLatitude,$startLongitude" +
                            "&ep=$toiletLatitude,$toiletLongitude" +
                            "&by=FOOT"
                )

                // 인텐트 생성 및 실행
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    addCategory(Intent.CATEGORY_BROWSABLE)
                }

                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "카카오맵 앱이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
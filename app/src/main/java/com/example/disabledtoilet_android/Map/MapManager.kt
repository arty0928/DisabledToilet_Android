package com.example.disabledtoilet_android.Map

import ToiletModel
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.disabledtoilet_android.Near.NearActivity
import com.kakao.vectormap.*
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.LatLng
import com.example.disabledtoilet_android.ToiletSearch.ToiletData
import com.example.disabledtoilet_android.R
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MapManager(private val context: Context) {

    private lateinit var mapView: MapView
    private lateinit var kakaoMap: KakaoMap
    private val labelToToiletMap = mutableMapOf<Label, ToiletModel>()

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


    // 지도 클릭 리스너 설정 함수
    private fun setupMapClickListener() {
        kakaoMap.setOnLabelClickListener { _, _, clickedLabel ->
            val toilet = labelToToiletMap[clickedLabel]
            if (toilet != null) {
                (context as NearActivity).bottomSheetHelper.initializeBottomSheet(toilet)
                true
            } else {
                false
            }
        }
    }

    // 화장실 위치로 카메라 이동 함수
    fun moveCameraToToilet(toiletData: ToiletModel) {
        val latitude = toiletData.wgs84_latitude
        val longitude = toiletData.wgs84_longitude
        if (latitude.toInt() != 0 && longitude.toInt() != 0) {
            val toiletPosition = LatLng.from(latitude, longitude)
            kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(toiletPosition, 16))
        } else {
            Toast.makeText(context, "위치데이터 준비 중 입니다.", Toast.LENGTH_SHORT).show()
        }
    }


    // 캐시된 위치로 카메라 이동 함수
    fun moveCameraToCachedLocation() {
        val sharedPreferences = context.getSharedPreferences("LocationCache", Context.MODE_PRIVATE)
        val cachedLatitude = sharedPreferences.getString("latitude", null)?.toDoubleOrNull()
        val cachedLongitude = sharedPreferences.getString("longitude", null)?.toDoubleOrNull()
        if (cachedLatitude != null && cachedLongitude != null) {
            val cachedPosition = LatLng.from(cachedLatitude, cachedLongitude)
            kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(cachedPosition, 16))
            addMarkerToMapCur(cachedPosition)
            Toast.makeText(context, "캐시된 현재 위치로 이동합니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "캐시된 현재 위치가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 현재 위치 마커 추가 함수
    private fun addMarkerToMapCur(position: LatLng): Label? {
        val styles = kakaoMap.labelManager?.addLabelStyles(
            LabelStyles.from(
                LabelStyle.from(R.drawable.cur2)
            )
        )
        val options = LabelOptions.from(position)
            .setStyles(styles)
            .setClickable(true)

        val layer = kakaoMap.labelManager?.layer
        return layer?.addLabel(options)
    }

    // 화장실 데이터 가져와 지도에 표시하는 함수
    fun fetchAndDisplayToiletData() {
        val toilets = ToiletData.getToiletAllData()
        if (toilets!!.isNotEmpty()) {
            toilets.forEach { toilet ->
                val pos = LatLng.from(toilet.wgs84_latitude, toilet.wgs84_longitude)
                if (toilet.wgs84_latitude != 0.0 && toilet.wgs84_longitude != 0.0) {
                    addMarkerToMapToilet(pos, toilet)
                }
            }
        } else {
            Log.e("MapManager", "No toilet data found in ToiletRepository.")
        }
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
        val toiletLatitude = toilet.wgs84_latitude
        val toiletLongitude = toilet.wgs84_longitude

        val uri = Uri.parse("kakaomap://route?" +
                "sp=${toiletLatitude},${toiletLongitude}" +
                "&ep=${toilet.wgs84_latitude},${toilet.wgs84_longitude}" +
                "&by=FOOT")

        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addCategory(Intent.CATEGORY_BROWSABLE)

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "카카오맵 앱이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
        }
    }

}
package com.example.disabledtoilet_android.Near

import ToiletModel
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.disabledtoilet_android.Detail.BottomSheetHelper
import com.example.disabledtoilet_android.Detail.DetailPageActivity
import com.example.disabledtoilet_android.Map.MapManager
import com.example.disabledtoilet_android.R
import com.example.disabledtoilet_android.Utility.Dialog.dialog.LoadingDialog
import com.example.disabledtoilet_android.Utility.Dialog.utils.KakaoShareHelper
import com.example.disabledtoilet_android.Utility.Dialog.utils.LocationHelper
import com.example.disabledtoilet_android.databinding.ActivityNearBinding
import com.kakao.vectormap.KakaoMapSdk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NearActivity : AppCompatActivity() {

    private var searchingToilet: ToiletModel? = null
    private lateinit var binding: ActivityNearBinding
    private val loadingDialog = LoadingDialog()
    val mapManager by lazy { MapManager(this) }


    private val locationHelper by lazy { LocationHelper(this) }
    val bottomSheetHelper by lazy { BottomSheetHelper(this) }
    val kakaoShareHelper by lazy { KakaoShareHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNearBinding.inflate(layoutInflater)
        KakaoMapSdk.init(this, "ce27585c8cc7c468ac7c46901d87199d")

        setContentView(binding.root)

        // 초기화 작업
        CoroutineScope(Dispatchers.Main).launch {
            val mapInitialized = mapManager.initializeMapView()
            if (mapInitialized) {
                locationHelper.checkLocationPermission {
                    // 현재 위치를 지도에 표시
                    locationHelper.setMapToCurrentLocation { currentPosition ->
                        if (currentPosition != null) {
                            mapManager.moveCameraToCachedLocation()
//                            Toast.makeText(this@NearActivity, "현재 위치로 이동합니다.", Toast.LENGTH_SHORT).show()
                        } else {
//                            Toast.makeText(this@NearActivity, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    fetchToiletDataAndDisplay()
                }

                // Intent 처리
                handleIntent()

                // 버튼 설정
                setupButtons()
            } else {
                Log.e("NearActivity", "Map initialization failed")
            }
        }
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }


    // Intent 데이터를 처리하는 함수
    private fun handleIntent() {
        val rootActivity = intent.getStringExtra("rootActivity")
        when (rootActivity) {
            "ToiletFilterSearchActivity" -> {
                val parcelableData = intent.getParcelableExtra<ToiletModel>("toiletData")
                if (parcelableData is ToiletModel) {
                    searchingToilet = parcelableData
                    mapManager.moveCameraToToilet(searchingToilet!!)
                    bottomSheetHelper.initializeBottomSheet(searchingToilet!!)
                } else {
                    Log.e("NearActivity", "parcelable data type is not matched")
                }
            }
            else -> Log.d("NearActivity", "root activity data is null")
        }
    }

    // 버튼 설정 함수
    private fun setupButtons() {
        findViewById<ImageButton>(R.id.map_return_cur_pos_btn).setOnClickListener {
            mapManager.moveCameraToCachedLocation()
        }
        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            onBackPressed()
        }
    }

    // 화장실 데이터를 가져와 지도에 표시하는 함수
    private fun fetchToiletDataAndDisplay() {
        CoroutineScope(Dispatchers.Main).launch {
            loadingDialog.show(supportFragmentManager, loadingDialog.tag)
            withContext(Dispatchers.IO) {
                mapManager.fetchAndDisplayToiletData()
            }
            loadingDialog.dismiss()
        }
    }
}
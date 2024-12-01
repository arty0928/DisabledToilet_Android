package com.dream.disabledtoilet_android.Near

import ToiletModel
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dream.disabledtoilet_android.BuildConfig
import com.dream.disabledtoilet_android.Detail.BottomSheetHelper
import com.dream.disabledtoilet_android.Map.MapManager
import com.dream.disabledtoilet_android.R
import com.dream.disabledtoilet_android.ToiletSearch.Adapter.ToiletListViewAdapter
import com.dream.disabledtoilet_android.User.ViewModel.UserViweModel
import com.dream.disabledtoilet_android.Utility.Dialog.dialog.LoadingDialog
import com.dream.disabledtoilet_android.Utility.Dialog.utils.KakaoShareHelper
import com.dream.disabledtoilet_android.Utility.Dialog.utils.LocationHelper
import com.dream.disabledtoilet_android.databinding.ActivityNearBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class NearActivity : AppCompatActivity() {

    private var searchingToilet: ToiletModel? = null
    private lateinit var binding: ActivityNearBinding
    private val loadingDialog = LoadingDialog()
    val mapManager by lazy { MapManager(this) }

    lateinit var userViewModel : UserViweModel

    private val locationHelper by lazy { LocationHelper(this) }
    val bottomSheetHelper by lazy { BottomSheetHelper(this) }
    val kakaoShareHelper by lazy { KakaoShareHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNearBinding.inflate(layoutInflater)
        KakaoMapSdk.init(this, BuildConfig.KAKAO_SCHEME)

        setContentView(binding.root)

        userViewModel = ViewModelProvider(this)[UserViweModel::class.java]

        CoroutineScope(Dispatchers.Main).launch {
            // 로딩 다이얼로그 표시
            if (supportFragmentManager.findFragmentByTag(loadingDialog.tag) == null) {
                loadingDialog.show(supportFragmentManager, loadingDialog.tag)
            }

            // 맵 초기화
            val mapInitialized = mapManager.initializeMapView()
            if (mapInitialized) {
                // 위치 권한 확인 및 사용자 위치 가져오기
                val position = locationHelper.getUserLocation() // getUserLocation 호출은 이제 suspend로 처리됨

                if (position != null) {
                    fetchToiletDataAndDisplay()

                    if (handleIntent() != "ToiletFilterSearchActivity"){
                        // 안넘어온거 확인 후 카메라 옮기기
                        mapManager.moveCameraToCachedLocation()
                    }

                    // 버튼 설정
                    setupButtons()
                } else {
                    Log.e("NearActivity", "Failed to get user location")
                    Toast.makeText(this@NearActivity, "위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }

                loadingDialog.dismiss()
            } else {
                Log.e("NearActivity", "Map initialization failed")
                loadingDialog.dismiss()
            }
        }
    }


    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }



    // Intent 데이터를 처리하는 함수
    private fun handleIntent(): String? {
        val rootActivity = intent.getStringExtra("rootActivity")
        when (rootActivity) {
            "ToiletFilterSearchActivity" -> {
                val parcelableData = intent.getParcelableExtra<ToiletModel>("toiletData")
                if (parcelableData is ToiletModel) {
                    searchingToilet = parcelableData
                    mapManager.moveCameraToToilet(searchingToilet!!)
                    bottomSheetHelper.initializeBottomSheet(searchingToilet!!)
                } else {
                    Log.e("test log", "parcelable data type is not matched")
                }
            }
            else -> Log.d("test log", "root activity data is null")
        }
        return rootActivity
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
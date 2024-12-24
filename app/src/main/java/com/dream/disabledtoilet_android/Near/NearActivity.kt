package com.dream.disabledtoilet_android.Near

import ToiletModel
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dream.disabledtoilet_android.BuildConfig
import com.dream.disabledtoilet_android.Detail.BottomSheetHelper
import com.dream.disabledtoilet_android.Map.MapManager
import com.dream.disabledtoilet_android.R
import com.dream.disabledtoilet_android.ToiletSearch.SearchFilter.FilterSearchDialog
import com.dream.disabledtoilet_android.ToiletSearch.ToiletData
import com.dream.disabledtoilet_android.ToiletSearch.ToiletFilterSearchActivity
import com.dream.disabledtoilet_android.ToiletSearch.ToiletRepository
import com.dream.disabledtoilet_android.ToiletSearch.ViewModel.FilterViewModel
import com.dream.disabledtoilet_android.User.ViewModel.UserViweModel
import com.dream.disabledtoilet_android.Utility.Dialog.dialog.LoadingDialog
import com.dream.disabledtoilet_android.Utility.Dialog.utils.KakaoShareHelper
import com.dream.disabledtoilet_android.Utility.Dialog.utils.LocationHelper
import com.dream.disabledtoilet_android.databinding.ActivityNearBinding
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
class NearActivity : AppCompatActivity() {

    private var searchingToilet: ToiletModel? = null
    private lateinit var binding: ActivityNearBinding
    private val loadingDialog = LoadingDialog()
    val mapManager by lazy { MapManager(this) }

    lateinit var userViewModel : UserViweModel

    private val locationHelper by lazy { LocationHelper(this) }
    val bottomSheetHelper by lazy { BottomSheetHelper(this) }
    val kakaoShareHelper by lazy { KakaoShareHelper(this) }

    /**
     * 필터
     */
    lateinit var filterSearchDialog: FilterSearchDialog
    lateinit var filterViewModel: FilterViewModel
    val toiletRepository = ToiletRepository()

    /**
     * ToiletData 한번 필터링한 리스트
     */
    lateinit var toiletList: MutableList<ToiletModel>
    var query = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNearBinding.inflate(layoutInflater)
        KakaoMapSdk.init(this, BuildConfig.KAKAO_SCHEME)

        setContentView(binding.root)

        userViewModel = ViewModelProvider(this)[UserViweModel::class.java]
        // 조건 적용 다이얼로그에서 사용할 뷰모델
        filterViewModel = ViewModelProvider(this)[FilterViewModel::class.java]

        // 혹시 화장실 리스트가 캐시되지 않았을 경우
        if (!ToiletData.toiletListInit) {
            Log.d("test log", "ToiletData toiletList 캐시 안됨")
            // 비어있는 리스트 생성
            toiletList = mutableListOf<ToiletModel>()
        } else {
            Log.d("test log", "ToiletData toiletList 캐시 됨")
            // 전처리 한번 해서 toiletList 생성
            toiletList = removeEmptyData(ToiletData.cachedToiletList!!.toMutableList())
        }

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

                    if (handleIntent() != "ToiletFilterSearchActivity"){
                        fetchToiletDataAndDisplay()
                        locationHelper.updateLocationCache(position)
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

        //조건 적용 다이얼로그 사라지면 필터 적용
        setFilterDialogObserver()
    }

    // Intent 데이터를 처리하는 함수
    private fun handleIntent(): String? {
        val rootActivity = intent.getStringExtra("rootActivity")
        when (rootActivity) {
            "ToiletFilterSearchActivity" -> {
                val parcelableData = intent.getParcelableExtra<ToiletModel>("toiletData")
                if (parcelableData is ToiletModel) {
                    searchingToilet = parcelableData
                    val position = LatLng.from(searchingToilet!!.wgs84_latitude, searchingToilet!!.wgs84_longitude)

                    mapManager.addMarkerToMapCur(position, "search")
                    mapManager.moveCameraToToilet(position)
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
        findViewById<Button>(R.id.filterButton_near).setOnClickListener {
            showFilterDialog()
        }
    }

    /**
     * 필터 다이얼로그 생성
     */
    fun showFilterDialog(){
        filterSearchDialog = FilterSearchDialog.newInstance()
        filterSearchDialog.show(supportFragmentManager, filterSearchDialog.tag)
        filterViewModel.isDialogDismissed.value = false
    }

    /**
     * 조건 검색 다이얼로그 UI 표출 옵저버
     */
    fun setFilterDialogObserver() {
        filterViewModel.isDialogDismissed.observe(this) { isDismissed ->
            if (isDismissed) {
                applyFilter()
            }
        }
    }

    /**
     * 조건 적용 다이얼로그 꺼지면 실행되는 함수
     * 다이얼로그 꺼지면 필터내용 바로 적용
     */
    fun applyFilter() {
        Log.d("test log", "[applyFilter]: dismissed")
        toiletRepository.setFilter(filterViewModel, toiletList.toList())

        fetchToiletDataAndDisplay()
    }

    /**
     * 비어있는 데이터는 삭제
     * 여기에 들어가야할 값은 현재 액티비티에 있는 toiletData
     */
    fun removeEmptyData(toiletList: MutableList<ToiletModel>): MutableList<ToiletModel> {
        for (i in toiletList.size - 1 downTo 0) {
            val toiletName = toiletList[i].restroom_name
            if (toiletName == "") {
                Log.d("test log", toiletList[i].toString())
                toiletList.removeAt(i)
            }
        }
        return toiletList
    }


    // 화장실 데이터를 가져와 지도에 표시하는 함수
    private fun fetchToiletDataAndDisplay() {
        Log.d("MapManager 12", "Fetching toilet data...")
        CoroutineScope(Dispatchers.Main).launch {
            loadingDialog.show(supportFragmentManager, loadingDialog.tag)
            withContext(Dispatchers.IO) {

                val filteredToilets = toiletRepository.getToiletWithSearchKeyword(toiletList, query)
                Log.d("MapManager 12", "Filtered toilets: ${filteredToilets.toString()}")

                mapManager.fetchAndDisplayFilteredToilets(filteredToilets)
            }
            loadingDialog.dismiss()
        }
    }

}
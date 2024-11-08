package com.example.disabledtoilet_android.ToiletSearch

import ToiletModel
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import kotlinx.coroutines.tasks.await
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.disabledtoilet_android.MainActivity
import com.example.disabledtoilet_android.ToiletSearch.Adapter.ToiletListViewAdapter
import com.example.disabledtoilet_android.ToiletSearch.SearchFilter.FilterSearchDialog
import com.example.disabledtoilet_android.ToiletSearch.SearchFilter.FilterViewModel
import com.example.disabledtoilet_android.Utility.Dialog.SortDialog
import com.example.disabledtoilet_android.Utility.Dialog.LoadingDialog
import com.example.disabledtoilet_android.databinding.ActivityToiletFilterSearchBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.kakao.vectormap.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
class ToiletFilterSearchActivity : AppCompatActivity() {
    lateinit var binding: ActivityToiletFilterSearchBinding
    val toiletRepository = ToiletRepository()
    lateinit var toiletListViewAdapter: ToiletListViewAdapter
    val loadingDialog = LoadingDialog()
    val sortDialog = SortDialog()

    // ToiletData 한번 필터링한 리스트
    lateinit var toiletList: MutableList<ToiletModel>

    var query = ""
    lateinit var filterSearchDialog: FilterSearchDialog

    lateinit var filterViewModel: FilterViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToiletFilterSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        filterViewModel = ViewModelProvider(this)[FilterViewModel::class.java]

        // 혹시 화장실 리스트가 캐시되지 않았을 경우
        if (!ToiletData.toiletListInit){
            Log.d("test log","ToiletData toiletList 캐시 안됨")
            // 비어있는 리스트 생성
            toiletList = mutableListOf<ToiletModel>()
        } else {
            Log.d("test log","ToiletData toiletList 캐시 됨")
            // 전처리 한번 해서 toiletList 생성
            toiletList = removeEmptyData(ToiletData.cachedToiletList!!.toMutableList())
        }

        setUi()

    }

    private fun setUi() {

        val context = this

        // 로딩 띄우기
        loadingDialog.show(supportFragmentManager, loadingDialog.tag)

        // 리사이클러뷰 어댑터는 비동기로 세팅
        CoroutineScope(Dispatchers.IO).launch {
            val userLocation: LatLng? = getUserLocation()
            val adapterList = mutableListOf<ToiletModel>()
            toiletListViewAdapter = ToiletListViewAdapter(adapterList, context, userLocation)
            Log.d("test log", "toiletListViewAdapter 생성")

            toiletListViewAdapter.updateList(
                toiletRepository.getToiletWithSearchKeyword(
                    toiletList,
                    query
                )
            )

            // 스레드 메인에서
            withContext(Dispatchers.Main){
                // 어댑터 초기화
                binding.toiletRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.toiletRecyclerView.adapter = toiletListViewAdapter
                getSearchKeyWord()
            }

            loadingDialog.dismiss()

        }

        binding.filterButton.setOnClickListener {
            showFilter()
        }
        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.filter.setOnClickListener {
            showSortDialog()
        }
        binding.toggle.setOnClickListener {
            showSortDialog()
        }

        filterViewModel.isDialogDismissed.observe(this) { isDismissed ->
            if (isDismissed) {
                applyFilter()
            }
        }
    }

    private fun getSearchKeyWord() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                query = binding.searchBar.text.toString()
                if (toiletList.isNotEmpty()) {
                    toiletListViewAdapter.updateList(
                        toiletRepository.getToiletWithSearchKeyword(
                            toiletList,
                            query
                        )
                    )
                } else {
                    Log.d("test log", "toiletList is empty")
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    private fun showSortDialog() {
        sortDialog.show(supportFragmentManager,loadingDialog.tag)
    }

    private fun showFilter() {
        filterSearchDialog = FilterSearchDialog.newInstance()
        filterSearchDialog.show(supportFragmentManager, filterSearchDialog.tag)
        filterViewModel.isDialogDismissed.value = false
    }

    private fun applyFilter() {
        Log.d("test log", "[applyFilter]: dismissed")
        toiletRepository.setFilter(filterViewModel, toiletList.toList())
        toiletListViewAdapter.updateList(
            toiletRepository.getToiletWithSearchKeyword(
                toiletList,
                query
            )
        )
    }

    private fun removeEmptyData(toiletList: MutableList<ToiletModel>): MutableList<ToiletModel>{
        for (i in toiletList.size-1 downTo 0){
            val toiletName = toiletList[i].restroom_name

            if (toiletName == ""){
                Log.d("test log",toiletList[i].toString())
                toiletList.removeAt(i)
            }
        }

        return toiletList
    }

    /**
     * 유저 위치 정보 받아 오기
     */
    private suspend fun getUserLocation(): LatLng? {
        var currentPosition: LatLng? = null

        // 권한부터 확인
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("test log", "LocationPermission Granted")
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            try {
                val location = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null).await()
                if (location != null) {
                    currentPosition = LatLng.from(location.latitude, location.longitude)
                    Log.d("test Log", "currentLocation: $currentPosition")
                } else {
                    Log.e("test Log", "Location is null")
                }
            } catch (e: Exception) {
                Log.e("test Log", "Failed to get location: ${e.message}")
                Toast.makeText(this, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("test log", "Location permission not granted")
        }

        return currentPosition
    }
}
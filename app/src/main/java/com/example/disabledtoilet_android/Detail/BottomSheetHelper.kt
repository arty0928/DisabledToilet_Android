package com.example.disabledtoilet_android.Detail

import ToiletModel
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.disabledtoilet_android.Near.NearActivity
import com.example.disabledtoilet_android.R
import com.example.disabledtoilet_android.ToiletSearch.ToiletData
import com.example.disabledtoilet_android.ToiletSearch.ViewModel.FilterViewModel
import com.example.disabledtoilet_android.User.ViewModel.UserViweModel
import com.example.disabledtoilet_android.Utility.Dialog.SaveManager
import com.example.disabledtoilet_android.Utility.Dialog.dialog.LoadingDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BottomSheetHelper(private val context: Context) {

    private val TAG = "BottomSheetHelper"
    private val saveManager = SaveManager(context) // ToiletManager 인스턴스 생성
    lateinit var userViweModel: UserViweModel

    /**
     * BottomSheet 초기화 함수
     */
    fun initializeBottomSheet(toilet: ToiletModel) {
        Log.d(TAG, "Initializing BottomSheet for toilet: ${toilet.restroom_name}")
        val bottomSheetView = (context as NearActivity).layoutInflater.inflate(R.layout.detail_bottomsheet, null)
        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        bottomSheetDialog.setContentView(bottomSheetView)

        // BottomSheet UI 설정
        setupBottomSheetUI(bottomSheetView, toilet)

        bottomSheetDialog.show()
    }


    /**
     * BottomSheet UI 설정
     */
    private fun setupBottomSheetUI(bottomSheetView: View, toilet: ToiletModel) {
        // UI 요소 초기화
        val toiletName: TextView = bottomSheetView.findViewById(R.id.toilet_name)
        val toiletAddress: TextView = bottomSheetView.findViewById(R.id.toilet_address)
        val toiletOpeningHours: TextView = bottomSheetView.findViewById(R.id.toilet_opening_hours)
        val saveIcon1: ImageView = bottomSheetView.findViewById(R.id.save_icon1)
        val saveIcon2: ImageView = bottomSheetView.findViewById(R.id.save_icon2)
        val saveCount: TextView = bottomSheetView.findViewById(R.id.toilet_save_count)
        val calDis : TextView = bottomSheetView.findViewById(R.id.toilet_distance)

        // 화장실 정보 설정
        toiletName.text = toilet.restroom_name
        toiletAddress.text = toilet.address_road ?: "-"
        toiletOpeningHours.text = toilet.opening_hours ?: "-"
        saveCount.text = "저장 (${toilet.save})"
        calDis.text = calculateDistance(toilet)

        // 최근 본 화장실에 추가
        (context as NearActivity).userViewModel.addRecentViewToilet(toilet)

        // 좋아요 버튼의 초기 상태 설정
        (context as NearActivity).userViewModel.likedToilets.observe(context as NearActivity, Observer { likedToilets ->
            val isLiked = likedToilets.any{it.number == toilet.number}
            val iconResource = if (isLiked) R.drawable.saved_star_icon else R.drawable.save_icon
            saveIcon1.setImageResource(iconResource)
            saveIcon2.setImageResource(iconResource)

        })


        // 좋아요 버튼 클릭 리스너
        val saveClickListener = View.OnClickListener {
            (context as NearActivity).userViewModel.toggleLikedToilet(toilet)
            Log.d(TAG, "Save button clicked for toilet: ${toilet.restroom_name}")
            saveCount.text = "저장 ${toilet.save}"
        }

        //좋아요 아이콘, 좋아요 갯수 업데이트
        bottomSheetView.findViewById<ImageView>(R.id.save_icon1).setOnClickListener(saveClickListener)
        bottomSheetView.findViewById<ImageView>(R.id.save_icon2).setOnClickListener(saveClickListener)

        // 상세 페이지로 이동
        bottomSheetView.findViewById<TextView>(R.id.more_button).setOnClickListener {
            val intent = Intent(context, DetailPageActivity::class.java)
            intent.putExtra("TOILET_DATA", toilet)
            context.startActivity(intent)
        }

        // 네비게이션 버튼
        bottomSheetView.findViewById<LinearLayout>(R.id.toilet_navigation_btn).setOnClickListener {
            Log.d(TAG, "Navigation button clicked")
            (context as NearActivity).mapManager.showKakaoMap(toilet)
        }

        // 공유 버튼
        bottomSheetView.findViewById<LinearLayout>(R.id.share_btn).setOnClickListener {
            Log.d(TAG, "Share button clicked")
            (context as NearActivity).kakaoShareHelper.shareKakaoMap(toilet)
        }
    }


    // 거리 계산 함수
    private fun calculateDistance(toilet: ToiletModel): String {
        val sharedPreferences = context.getSharedPreferences("LocationCache", Context.MODE_PRIVATE)
        val currentLatitude = sharedPreferences.getString("latitude", null)?.toDoubleOrNull()
        val currentLongitude = sharedPreferences.getString("longitude", null)?.toDoubleOrNull()

        return if (currentLatitude != null && currentLongitude != null) {
            val currentLocation = Location("").apply {
                latitude = currentLatitude
                longitude = currentLongitude
            }
            val toiletLocation = Location("").apply {
                latitude = toilet.wgs84_latitude
                longitude = toilet.wgs84_longitude
            }
            val distanceInMeters = currentLocation.distanceTo(toiletLocation)
            if (distanceInMeters < 1000) "${distanceInMeters.toInt()}m" else String.format("%.1fkm", distanceInMeters / 1000)
        } else {
            "-"
        }
    }
}
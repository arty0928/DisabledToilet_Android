package com.example.disabledtoilet_android.Detail

import ToiletModel
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.disabledtoilet_android.Detail.DetailPageActivity
import com.example.disabledtoilet_android.Near.NearActivity
import com.example.disabledtoilet_android.R
import com.google.android.material.bottomsheet.BottomSheetDialog

class BottomSheetHelper(private val context: Context) {

    // BottomSheet 초기화 함수
    fun initializeBottomSheet(toilet: ToiletModel) {
        Log.d("BottomSheetHelper", "Initializing BottomSheet for toilet: ${toilet.restroom_name}")
        val bottomSheetView = (context as NearActivity).layoutInflater.inflate(R.layout.detail_bottomsheet, null)
        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
        bottomSheetDialog.setContentView(bottomSheetView)

        bottomSheetView.findViewById<TextView>(R.id.toilet_name).text = toilet.restroom_name
        bottomSheetView.findViewById<TextView>(R.id.toilet_address).text = toilet.address_road ?: "-"
        bottomSheetView.findViewById<TextView>(R.id.toilet_opening_hours).text = toilet.opening_hours ?: "-"
        bottomSheetView.findViewById<TextView>(R.id.toilet_distance).text = calculateDistance(toilet)

        bottomSheetDialog.show()

        bottomSheetView.findViewById<TextView>(R.id.more_button).setOnClickListener {
            val intent = Intent(context, DetailPageActivity::class.java)
            intent.putExtra("TOILET_DATA", toilet)
            context.startActivity(intent)
            bottomSheetDialog.dismiss()
        }

        bottomSheetView.findViewById<LinearLayout>(R.id.toilet_navigation_btn).setOnClickListener {
            Log.d("BottomSheetHelper", "Navigation button clicked")
            (context as NearActivity).mapManager.showKakaoMap(toilet)
        }

        bottomSheetView.findViewById<LinearLayout>(R.id.share_btn).setOnClickListener {
            Log.d("BottomSheetHelper", "Share button clicked")
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
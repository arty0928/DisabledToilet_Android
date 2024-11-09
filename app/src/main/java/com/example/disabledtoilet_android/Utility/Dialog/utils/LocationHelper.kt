package com.example.disabledtoilet_android.Utility.Dialog.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.disabledtoilet_android.Near.NearActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.kakao.vectormap.LatLng

class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // 위치 권한을 확인하고 설정하는 함수
    fun checkLocationPermission(onLocationReady: () -> Unit) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                context as NearActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                NearActivity.LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            onLocationReady()
        }
    }

    // 현재 위치를 설정하고 캐시를 업데이트하는 함수
    fun setMapToCurrentLocation(onComplete: (LatLng?) -> Unit) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val currentPosition = LatLng.from(location.latitude, location.longitude)
                        updateLocationCache(currentPosition)
                        onComplete(currentPosition) // 현재 위치 반환
                    } else {
                        onComplete(null) // 위치를 가져올 수 없음
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    onComplete(null) // 위치를 가져올 수 없음
                }
        } else {
            onComplete(null) // 권한 없음
        }
    }

    // 캐시 업데이트 함수
    private fun updateLocationCache(currentPosition: LatLng) {
        val sharedPreferences = context.getSharedPreferences("LocationCache", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("latitude", currentPosition.latitude.toString())
            putString("longitude", currentPosition.longitude.toString())
            apply()
        }
    }
}

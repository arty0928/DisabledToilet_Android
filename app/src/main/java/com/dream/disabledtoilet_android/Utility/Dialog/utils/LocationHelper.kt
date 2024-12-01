package com.dream.disabledtoilet_android.Utility.Dialog.utils

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.dream.disabledtoilet_android.Near.NearActivity
import com.dream.disabledtoilet_android.ToiletSearch.Adapter.ToiletListViewAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.kakao.vectormap.LatLng
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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

    // 캐시된 위치 가져오기 함수
    fun getCachedLocation(): LatLng? {
        val sharedPreferences = context.getSharedPreferences("LocationCache", Context.MODE_PRIVATE)
        val latitude = sharedPreferences.getString("latitude", null)?.toDoubleOrNull()
        val longitude = sharedPreferences.getString("longitude", null)?.toDoubleOrNull()
        return if (latitude != null && longitude != null) LatLng.from(latitude, longitude) else null
    }

    // 현재 위치를 가져오는 함수
//    suspend fun getUserLocation(): LatLng? {
//        var currentPosition: LatLng? = null
//
//        // 권한 확인 후, 권한이 있을 때 위치를 가져오는 비동기 작업
//        suspendCoroutine<LatLng?> { continuation ->
//            checkLocationPermission {
//                // 권한이 있음
//                Log.d("test log", "LocationPermission Granted")
//                try {
//                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
//
//                    // 캐시된 위치가 없으면 현재 위치를 가져오고 캐시 업데이트
//                    if (getCachedLocation() == null) {
//                        // 로케이션 받아올때까지 await() (코루틴 안에서 호출)
//                        fusedLocationClient.getCurrentLocation(
//                            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
//                            null
//                        ).addOnSuccessListener { location ->
//                            currentPosition = LatLng.from(location.latitude, location.longitude)
//                            updateLocationCache(currentPosition!!) // 위치 캐시 업데이트
//                            continuation.resume(currentPosition)
//                        }.addOnFailureListener { e ->
//                            Log.e("test Log", "Failed to get location: ${e.message}")
//                            Toast.makeText(context, "현재 위치를 가져올 수 없습니다. 1", Toast.LENGTH_SHORT).show()
//                            continuation.resume(null)
//                        }
//                    } else {
//                        currentPosition = getCachedLocation()
//                        continuation.resume(currentPosition)
//                    }
//                } catch (e: Exception) {
//                    Log.e("test Log", "Failed to get location: ${e.message}")
//                    Toast.makeText(context, "현재 위치를 가져올 수 없습니다. 2", Toast.LENGTH_SHORT).show()
//                    continuation.resume(null)
//                }
//            }
//        }
//
//        return currentPosition
//    }
    suspend fun getUserLocation(): LatLng? {
        var currentPosition: LatLng? = null
        // 권한부터 확인
        if (ActivityCompat.checkSelfPermission(
                context,
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // 권한 있음
            Log.d("test log", "LocationPermission Granted")
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            try {
                // 로케이션 받아올때까지 await()
                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    null
                ).await()
                //currentPosition 생성
                currentPosition = LatLng.from(location.latitude, location.longitude)
            } catch (e: Exception) {
                Log.e("test Log", "Failed to get location: ${e.message}")
                Toast.makeText(context, "현재 위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // 권한 없음
            Log.e("test log", "Location permission not granted")
        }
        Log.d("test log", "Location: " + currentPosition.toString())
        return currentPosition
    }

    // 캐시 업데이트 함수
    fun updateLocationCache(currentPosition: LatLng) {
        val sharedPreferences = context.getSharedPreferences("LocationCache", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("latitude", currentPosition.latitude.toString())
            putString("longitude", currentPosition.longitude.toString())
            apply()
        }
    }
}

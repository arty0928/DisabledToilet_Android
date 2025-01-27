package com.dream.disabledtoilet_android.Near.DataLayer

import com.dream.disabledtoilet_android.ToiletSearch.Model.ToiletModel
import android.location.Location
import android.util.Log
import com.kakao.vectormap.camera.CameraPosition

class ToiletListGenerator {
    /**
     *      카메라 위치 기준으로 10kM 이내의 com.dream.disabledtoilet_android.ToiletSearch.Model.ToiletModel 리스트 생성
     */
    fun makeToiletListInCamera(cameraPosition: CameraPosition, toiletList: List<ToiletModel>): List<ToiletModel> {
        val resultList = mutableListOf<ToiletModel>()
        for (i in toiletList.indices){
            val toiletLatitude = toiletList.get(i).wgs84_latitude
            val toiletLongitude = toiletList.get(i).wgs84_longitude
            val toiletLocation = Location("").apply {
                latitude = toiletLatitude
                longitude = toiletLongitude
            }
            val cameraLatitude = cameraPosition.position.latitude
            val cameraLongitude = cameraPosition.position.longitude
            val cameraLocation = Location("").apply {
                latitude = cameraLatitude
                longitude = cameraLongitude
            }
            val distance = calculateDistance(cameraLocation, toiletLocation)
            if (distance < 1000){
                resultList.add(toiletList.get(i))
            }
        }
        Log.d("test log", "카메라 안 화장실 수: ${resultList.size}")
        return resultList
    }

    fun calculateDistance(position1: Location, position2: Location): Float {
        return position1.distanceTo(position2)
    }
}
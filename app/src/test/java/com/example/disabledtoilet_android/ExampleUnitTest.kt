package com.example.disabledtoilet_android

import android.util.Log
import com.example.disabledtoilet_android.ToiletSearch.ToiletRepository
import com.example.disabledtoilet_android.Utility.KaKaoAPI.KakaoApiRepository
import com.kakao.vectormap.LatLng
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.test.runTest
import org.junit.Test

import org.junit.Assert.*

class ExampleUnitTest {
    val testLocation = LatLng.from(37.5447087, 127.0744097)

    @Test
    fun testGetToiletByLotAddress(){

    }

    @Test
    fun getAddressByCoordinate() = runTest{
        val kakaoRepository = KakaoApiRepository.KakaoLocalRepository()

        System.out.println(kakaoRepository.getAddressFromCoordinate(testLocation.longitude,testLocation.latitude).toString() )
    }

}
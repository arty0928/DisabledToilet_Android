package com.dream.disabledtoilet_android

import com.dream.disabledtoilet_android.Utility.KaKaoAPI.KakaoApiRepository
import com.kakao.vectormap.LatLng
import kotlinx.coroutines.test.runTest
import org.junit.Test

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
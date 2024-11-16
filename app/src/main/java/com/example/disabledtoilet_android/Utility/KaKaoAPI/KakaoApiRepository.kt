package com.example.disabledtoilet_android.Utility.KaKaoAPI

import com.example.disabledtoilet_android.Utility.KaKaoAPI.Model.AddressDocument
import com.example.disabledtoilet_android.Utility.KaKaoAPI.Model.KakaoApiResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class KakaoApiRepository {
    /**
     * RestApi 중 로컬
     */
    class KakaoLocalRepository {
        val KAKAO_API_KEY = "aad7f57f316e4302e5fb94b61b7a811c"
        private val retrofit = Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        private val apiService = retrofit.create(KakaoApiService::class.java)

        /**
         * 좌표로 주소 변환하기
         */
        suspend fun getAddressFromCoordinate(x: Double, y: Double): Result<KakaoApiResponse<AddressDocument>> {
            return try {
                val response = apiService.getAddress(
                    x = x.toString(),
                    y = y.toString(),
                    authorization = "KakaoAK $KAKAO_API_KEY"
                )
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
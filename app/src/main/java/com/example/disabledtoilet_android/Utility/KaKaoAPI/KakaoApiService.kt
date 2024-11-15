package com.example.disabledtoilet_android.Utility.KaKaoAPI

import com.example.disabledtoilet_android.Utility.KaKaoAPI.Model.AddressDocument
import com.example.disabledtoilet_android.Utility.KaKaoAPI.Model.KakaoApiResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 *  카카오 Rest Api
 */
interface KakaoApiService {
    /**
     * 좌표로 주소 얻기.
     * 예제: curl -v -G GET "https://dapi.kakao.com/v2/local/geo/coord2address.json?x=127.423084873712&y=37.0789561558879&input_coord=WGS84" \
     *   -H "Authorization: KakaoAK ${REST_API_KEY}"
     */
    @GET("v2/local/geo/coord2address.json")
    suspend fun getAddress(
        @Query("x") x: String,
        @Query("y") y: String,
        @Query("input_coord") inputCoord: String = "WGS84",
        @Header("Authorization") authorization: String
    ): KakaoApiResponse<AddressDocument>
}
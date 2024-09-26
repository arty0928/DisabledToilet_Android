package com.example.disabledtoilet_android.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("getPublicToiletById/{id}") // {id}를 URL의 파라미터로 사용
    fun getPublicToiletById(@Path("id") toiletId: Int): Call<ToiletDataResponse>
}
package com.example.disabledtoilet_android.api

import android.content.Context
import com.example.disabledtoilet_android.R
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance{

    private lateinit var retrofit : Retrofit
    lateinit var api : ApiService

    fun initRetrofit(context : Context){
        val baseUrl = "http://ec2-13-125-103-74.ap-northeast-2.compute.amazonaws.com:8083/api/"


        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ApiService::class.java)

    }

}
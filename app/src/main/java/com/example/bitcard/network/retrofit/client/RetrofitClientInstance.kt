package com.example.bitcard.network.retrofit.client

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitHelper {

    private var okHttpClient = OkHttpClient.Builder().build()

    fun getRetrofitInstance(): Retrofit {
       return Retrofit.Builder().baseUrl("http://192.168.1.1:3000/api/v1/")
           .addConverterFactory(GsonConverterFactory.create())
           .client(okHttpClient)
           .build()
    }
}
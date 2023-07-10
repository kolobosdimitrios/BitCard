package com.example.bitcard.network.retrofit.client

import com.example.bitcard.network.retrofit.api.BitcardApiV1
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitHelper{

    private const val HEROKU_BAE_URL = "https://bitcard.herokuapp.com//api/v1/"
    private const val LOCAL_BASE_URL = "http://192.168.1.1:3000//api/v1/"

    private var okHttpClient = OkHttpClient.Builder().build()
    val newInstance: BitcardApiV1

    init {
        val retrofit = Retrofit.Builder().baseUrl(HEROKU_BAE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()

        newInstance = retrofit.create(BitcardApiV1::class.java)
    }

}
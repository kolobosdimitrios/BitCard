package com.example.bitcard.network.retrofit.client

import com.example.bitcard.network.retrofit.api.BitcardApiV1
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitHelper {

    private var okHttpClient = OkHttpClient.Builder().build()
    val newInstance: BitcardApiV1

    init {
        val retrofit = Retrofit.Builder().baseUrl("https://bitcard.herokuapp.com//api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        newInstance = retrofit.create(BitcardApiV1::class.java)
    }

}
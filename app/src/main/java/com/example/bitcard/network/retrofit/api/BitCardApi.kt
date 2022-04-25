package com.example.bitcard.network.retrofit.api

import com.example.bitcard.network.daos.responses.SimpleResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.POST


interface BitCardApi {


    @POST("/users")
    fun register(jsonObject: JSONObject) : Call<SimpleResponse>

}
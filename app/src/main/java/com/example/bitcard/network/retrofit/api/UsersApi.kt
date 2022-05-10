package com.example.bitcard.network.retrofit.api

import com.example.bitcard.network.daos.requests.RegisterModel
import com.example.bitcard.network.daos.requests.Request
import com.example.bitcard.network.daos.responses.SimpleResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface UsersApi {

    @Headers("Content-Type: application/json")
    @POST("users/create")
    fun register(@Body requestData: RegisterModel) : Call<SimpleResponse>

}
package com.example.bitcard.network.retrofit.api

import com.example.bitcard.network.daos.requests.RegisterModel
import com.example.bitcard.network.daos.responses.GetUserResponse
import com.example.bitcard.network.daos.responses.SimpleResponse
import retrofit2.Call
import retrofit2.http.*


interface UsersApi {

    @Headers("Content-Type: application/json")
    @POST("users/create")
    fun register(@Body requestData: RegisterModel) : Call<SimpleResponse>

    @GET("users/login/")
    fun get(@Query("user_id") uid: String): Call<GetUserResponse>

    @GET("users/logout/")
    fun destroyUser(@Query("user_id") uid: String) : Call<SimpleResponse>

}
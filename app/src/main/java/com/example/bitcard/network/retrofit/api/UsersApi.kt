package com.example.bitcard.network.retrofit.api

import com.example.bitcard.network.daos.requests.RegisterModel
import com.example.bitcard.network.daos.requests.Request
import com.example.bitcard.network.daos.requests.UserIdModel
import com.example.bitcard.network.daos.requests.UserModel
import com.example.bitcard.network.daos.responses.GetUserResponse
import com.example.bitcard.network.daos.responses.SimpleResponse
import retrofit2.Call
import retrofit2.http.*


interface UsersApi {

    @Headers("Content-Type: application/json")
    @POST("users/create")
    fun register(@Body requestData: RegisterModel) : Call<SimpleResponse>

    @GET("users/show_with_id/")
    fun get(@Query("user_id") uid: String): Call<GetUserResponse>

}
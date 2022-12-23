package com.example.bitcard.network.retrofit.api

import com.example.bitcard.network.daos.requests.RegisterModel
import com.example.bitcard.network.daos.responses.GetUserResponse
import com.example.bitcard.network.daos.responses.SimpleResponse
import com.example.bitcard.network.daos.responses.TokenResponse
import retrofit2.Call
import retrofit2.http.*


interface BitcardApiV1 {

    @Headers("Content-Type: application/json")
    @POST("users/")
    fun register(@Body requestData: RegisterModel) : Call<GetUserResponse>

    @GET("users/{id}")
    fun get(@Path("id")userID : Long) : Call<GetUserResponse>

    @POST("users/login/")
    fun login(@Query("user_key")user_key: String): Call<GetUserResponse>

    @POST("users/logout/")
    fun logout(/*@Query("user_id") id: Long*/) : Call<SimpleResponse>

    @Headers("Content-Type: application/json")
    @GET("users/{id}/tokens/get/")
    fun getToken(@Path("id")userID : Long, @Query("user_key") userKey: String) : Call<TokenResponse>

}
package com.example.bitcard.network.retrofit.api

import com.example.bitcard.network.daos.requests.RegisterModel
import com.example.bitcard.network.daos.responses.*
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
    @GET("users/{id}/tokens_get")
    fun getToken(@Path("id")userID : Long) : Call<TokenResponse>

    @GET("users/{id}/tokens/")
    fun getUserTokens(@Path("id")userID : Long) : Call<TokenListResponse>

    @GET("users/{user_id}/tokens/{token_id}/purchases")
    fun getTokensPurchases(@Path("user_id") user_id : Long, @Path("token_id") token_id: Long) : Call<PurchasesListResponse>

    @GET("users/{user_id}/tokens/{token_id}/purchases/{purchase_id}/products")
    fun getPurchaseProducts(@Path("user_id") user_id : Long, @Path("token_id") token_id: Long, @Path("purchase_id") purchase_id: Long) : Call<ProductListResponse>

}
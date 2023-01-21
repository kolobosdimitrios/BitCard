package com.example.bitcard.network.retrofit.api

import com.example.bitcard.network.daos.requests.RegisterModel
import com.example.bitcard.network.daos.requests.Token
import com.example.bitcard.network.daos.responses.*
import com.example.bitcard.network.daos.responses.models.Product
import com.example.bitcard.network.daos.responses.models.Purchase
import com.example.bitcard.network.daos.responses.models.Shop
import okhttp3.MultipartBody
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
    fun getUserTokens(@Path("id")userID : Long) : Call<List<Token>>

    @GET("users/{user_id}/tokens/{token_id}/purchases")
    fun getTokensPurchases(@Path("user_id") user_id : Long, @Path("token_id") token_id: Long) : Call<List<Purchase>>

    @GET("users/{user_id}/tokens/{token_id}/purchases/{purchase_id}/products")
    fun getPurchaseProducts(@Path("user_id") user_id : Long, @Path("token_id") token_id: Long, @Path("purchase_id") purchase_id: Long) : Call<List<Product>>

    @PUT("users/{user_id}")
    fun updateUsersProfilePicture(@Path("user_id") user_id: Long, @Body registerModel: RegisterModel) : Call<SimpleResponse>

    @GET("shops/")
    fun getShops() : Call<List<Shop>>

    @GET("shops/{shop_id}")
    fun getShop(@Path("shop_id") shop_id: Long) : Call<Shop>

}
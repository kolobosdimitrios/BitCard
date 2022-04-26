package com.example.bitcard.network.daos.requests

import com.google.gson.annotations.SerializedName

data class RegisterModel(
    @SerializedName("user")
    val userModel: UserModel
)
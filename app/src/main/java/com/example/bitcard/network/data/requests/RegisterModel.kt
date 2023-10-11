package com.example.bitcard.network.data.requests

import com.google.gson.annotations.SerializedName

data class RegisterModel(
    @SerializedName("user")
    val userModel: UserDataSenderObj
)
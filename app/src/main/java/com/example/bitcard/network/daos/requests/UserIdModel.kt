package com.example.bitcard.network.daos.requests

import com.google.gson.annotations.SerializedName

data class UserIdModel (
    @SerializedName("user_id")
    val userId: String
    )
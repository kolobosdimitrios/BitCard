package com.example.bitcard.network.daos.responses

import com.example.bitcard.network.daos.requests.UserModel

data class GetUserResponse(
    val status_code : Int,
    val description: String,
    val data: UserModel
)
package com.example.bitcard.network.daos.responses

import com.example.bitcard.db.entities.User

data class GetUserResponse(
    val status_code : Int,
    val description: String,
    val data: User
)
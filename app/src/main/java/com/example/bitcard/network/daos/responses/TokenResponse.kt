package com.example.bitcard.network.daos.responses

import com.example.bitcard.network.daos.requests.Token

class TokenResponse(
    val status_code : Int,
    val description: String,
    val token: Token
) {
}
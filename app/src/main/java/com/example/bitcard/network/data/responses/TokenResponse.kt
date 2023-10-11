package com.example.bitcard.network.data.responses

import com.example.bitcard.network.data.requests.Token

class TokenResponse(
    val status_code : Int,
    val description: String,
    val data: Token
) {
}
package com.example.bitcard.network.data.responses

import com.example.bitcard.network.data.requests.Token


class TokenListResponse(
    val tokens: List<Token>
) {

    fun isEmpty() : Boolean = tokens.isEmpty()

    fun size() : Int = tokens.size

}
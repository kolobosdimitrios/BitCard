package com.example.bitcard.network.daos.responses

import com.example.bitcard.network.daos.requests.Token


class TokenListResponse(
    private val tokens: List<Token>
) {

    fun isEmpty() : Boolean = tokens.isEmpty()

    fun size() : Int = tokens.size

}
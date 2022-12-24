package com.example.bitcard.network.daos.responses.models

class Purchase(
    val id: Long,
    val created_at: String,
    val updated_at: String,
    val tokensId: Long,
    val productsId: Long
) {
}
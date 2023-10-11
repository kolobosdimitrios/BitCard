package com.example.bitcard.network.data.requests

data class FavoriteShopModel(
    val id: Long? = null,
    val user_id: Long,
    val shop_id: Long,
    val created_at: String? = null,
    val updated_at: String? = null
) {
}
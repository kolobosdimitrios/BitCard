package com.example.bitcard.adapters

import com.example.bitcard.network.daos.responses.models.Purchase

class PurchaseRecyclerModel(
    private val purchases: List<Purchase>
) {

    fun getTitle() = purchases[0].created_at

    fun getProductsIds() : Array<Long>{

        val ids = Array(purchases.size) {
            purchases[it].id
        }

        return ids
    }

}
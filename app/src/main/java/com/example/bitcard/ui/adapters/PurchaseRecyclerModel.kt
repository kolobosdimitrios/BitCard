package com.example.bitcard.ui.adapters

import com.example.bitcard.Time
import com.example.bitcard.network.data.responses.models.Purchase

class PurchaseRecyclerModel(
    private val purchases: List<Purchase>
) {

    fun getTitle() = Time.format( purchases[0].created_at )

    fun getProductsIds() : Array<Long>{

        val ids = Array(purchases.size) {
            purchases[it].id
        }

        return ids
    }

    fun getTokenId() : Long {

        return purchases[0].tokens_id
    }

}
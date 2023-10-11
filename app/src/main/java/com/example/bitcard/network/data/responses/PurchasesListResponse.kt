package com.example.bitcard.network.data.responses

import com.example.bitcard.network.data.responses.models.Purchase

class PurchasesListResponse(
    val status_code : Long,
    val description : String,
    val purchases : List<Purchase>
) {
}
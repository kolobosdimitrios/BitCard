package com.example.bitcard.network.daos.responses

import com.example.bitcard.network.daos.responses.models.Purchase

class PurchasesListResponse(
    val status_code : Long,
    val description : String,
    val purchases : List<Purchase>
) {
}
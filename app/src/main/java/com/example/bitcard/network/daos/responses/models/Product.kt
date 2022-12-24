package com.example.bitcard.network.daos.responses.models

class Product(
    val id: Long,
    val name: String,
    val value: Double,
    val description: String,
    val code: String,
    val barcode: String,
    val created_at: String,
    val updated_at: String,
    val shops_id: Long
) {
}
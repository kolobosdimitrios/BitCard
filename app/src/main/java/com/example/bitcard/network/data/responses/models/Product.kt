package com.example.bitcard.network.data.responses.models

class Product(
    val id: Long,
    val name: String,
    val value: Double,
    val description: String,
    val code: String,
    val barcode: String,
    val created_at: String,
    val updated_at: String,
    val shops_id: Long,
    val image: String?
) {
    override fun toString(): String {
        return "Product(id=$id, name='$name', value=$value, description='$description', code='$code', barcode='$barcode', created_at='$created_at', updated_at='$updated_at', shops_id=$shops_id)"
    }

}
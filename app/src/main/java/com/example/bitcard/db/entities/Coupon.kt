package com.example.bitcard.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coupons")
data class Coupon(
    @PrimaryKey
    val id: Long,
    val value: Double,
    val redeemed: Int,
    val user_id: Long,
    val code: String,
    val created_at: String,
    val updated_at: String
) {
}
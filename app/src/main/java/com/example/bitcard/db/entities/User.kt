package com.example.bitcard.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "surname")
    val surname: String,
    @ColumnInfo(name = "username")
    val username: String,
    @ColumnInfo(name = "userId")
    @SerializedName("user_key")
    var userId: String,
    @ColumnInfo(name = "address")
    val address: String,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "date_of_birth")
    @SerializedName("date_of_birth")
    val dateOfBirth: String,
    @ColumnInfo(name = "image_blob_string", typeAffinity = ColumnInfo.BINARY)
    var image: String?,
    @ColumnInfo(name = "points")
    @SerializedName("points")
    var points: Int
) {
}
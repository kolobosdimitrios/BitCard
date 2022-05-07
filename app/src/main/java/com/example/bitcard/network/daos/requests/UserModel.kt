package com.example.bitcard.network.daos.requests

import com.google.gson.annotations.SerializedName

/**
 * POST
 */
data class UserModel(
    @SerializedName("name")
    val name: String,
    @SerializedName("surname")
    val surname: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("password_confirmation")
    val passwordConfirm: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("date_of_birth")
    val dateOfBirth: String
) : Request<UserModel> {
    override fun getRequestData(): UserModel {
        return this
    }
}
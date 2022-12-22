package com.example.bitcard.network.daos.requests

import com.google.gson.annotations.SerializedName

/**
 * POST
 */
class UserModel(
    val id: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("surname")
    val surname: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("user_key")
    var userId: String,
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

    override fun toString(): String {
        return "UserModel(name='$name', surname='$surname', username='$username', userId='$userId', address='$address', email='$email', dateOfBirth='$dateOfBirth', id='$id')"
    }


}
package com.example.bitcard.network.daos.requests

import com.google.gson.annotations.SerializedName

/**
 * POST
 */
class UserDataSenderObj(
    var id: Long? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("surname")
    var surname: String? = null,
    @SerializedName("username")
    var username: String? = null,
    @SerializedName("user_key")
    var userId: String? = null,
    @SerializedName("address")
    var address: String? = null,
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("date_of_birth")
    var dateOfBirth: String? = null,
    @SerializedName("image")
    var image: String? = null
) : Request<UserDataSenderObj> {
    override fun getRequestData(): UserDataSenderObj {
        return this
    }

    override fun toString(): String {
        return "UserModel(name='$name', surname='$surname', username='$username', userId='$userId', address='$address', email='$email', dateOfBirth='$dateOfBirth', id='$id')"
    }


}
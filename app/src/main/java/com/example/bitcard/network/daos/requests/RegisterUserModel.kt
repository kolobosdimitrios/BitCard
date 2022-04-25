package com.example.bitcard.network.daos.requests

import org.json.JSONObject

/**
 * POST
 */
data class RegisterUserModel(
    val name: String,
    val surname: String,
    val username: String,
    val password: String,
    val passwordConfirm: String,
    val address: String,
    val email: String,
    val dateOfBirth: String
) : Request {
    override fun toJsonFormat(): JSONObject {
        var jsonObject = JSONObject()
        var userJsonObject = JSONObject()
        userJsonObject.put("name", name)
        userJsonObject.put("surname", surname)
        userJsonObject.put("username", username)
        userJsonObject.put("password", password);
        userJsonObject.put("password_confirmation", passwordConfirm)
        userJsonObject.put("address", address)
        userJsonObject.put("date_of_birth", dateOfBirth)
        userJsonObject.put("email", email)
        jsonObject.put("user", userJsonObject)
        return jsonObject;
    }
}
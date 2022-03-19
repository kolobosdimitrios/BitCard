package com.example.bitcard.network.daos.requests

import com.example.bitcard.network.daos.KeyObject
import org.json.JSONObject
import java.lang.Exception

class CreateCardRequestModel(
    private val name : String,
    private val surname : String,
    private val address : String
) : Request {


    override fun toJsonFormat(keys: List<KeyObject>): JSONObject {
        val jsonObject = JSONObject()
        try{
            jsonObject.put("", name)
            jsonObject.put("", surname)
            jsonObject.put("", address)
        }catch (exception : Exception){
            return JSONObject()
        }

        return jsonObject
    }
}
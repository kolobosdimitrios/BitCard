package com.example.bitcard

import org.json.JSONObject

object JSONParser {

    fun parseString(jsonString: String): JSONObject {

        return JSONObject(jsonString)
    }

    fun getValue(key : String, jsonObject: JSONObject) : Any {
        return jsonObject.get(key)
    }
}
package com.example.bitcard.network.daos.requests

import com.example.bitcard.network.daos.KeyObject
import org.json.JSONObject

interface Request {

    fun toJsonFormat() : JSONObject
}
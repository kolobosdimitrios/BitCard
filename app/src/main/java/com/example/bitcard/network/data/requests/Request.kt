package com.example.bitcard.network.data.requests

interface Request<T> {

    fun getRequestData() : T
}
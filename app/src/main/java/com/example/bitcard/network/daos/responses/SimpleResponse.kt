package com.example.bitcard.network.daos.responses

data class SimpleResponse<T>(

    val status_code : Int,
    val description: String,
    val data: T

) {

    companion object ResponseCodes{

        const val STATUS_OK = 1
        const val STATUS_IGNORE = 2
        const val STATUS_ERROR = -1

    }


}
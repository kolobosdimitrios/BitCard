package com.example.bitcard.network.daos.responses

data class SimpleResponse(

    val status_code : Int,
    val description: String,
    val data: Any

) {

    companion object ResponseCodes{

        const val STATUS_OK = 1
        const val STATUS_IGNORE = 2
        const val STATUS_ERROR = -1

    }


}
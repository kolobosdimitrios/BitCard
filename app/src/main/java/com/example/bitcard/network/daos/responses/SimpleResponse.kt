package com.example.bitcard.network.daos.responses

data class SimpleResponse(

    val status_code : Long,
    val description: String,
    val data: Any

) {

    companion object ResponseCodes{

        const val STATUS_OK = 1
        const val STATUS_IGNORE = 2;
        const val STATUS_ERROR = -1;

    }

    fun getIgnoreStatus() = STATUS_IGNORE;
    fun getOKStatus() = STATUS_OK;
    fun getErrorStatus() = STATUS_ERROR;

}
package com.example.bitcard.network.data.responses.models

import com.google.gson.annotations.Expose


data class Shop(
    var id: Long,
    val shop_name : String,
    val location_name : String,
    val location_latitude : Float,
    val location_longitude : Float,
    val description : String,
    val location_address : String,
    val created_at : String,
    val updated_at : String,
    val working_hours : String,
    val contact_info : String,
    @Expose
    var distanceFromUser: Double?
){
    override fun toString(): String {
        return "Shop(id=$id, shop_name='$shop_name', location_name='$location_name', location_latitude=$location_latitude, location_longitude=$location_longitude, description='$description', location_address='$location_address', created_at='$created_at', updated_at='$updated_at', working_hours='$working_hours', contact_info='$contact_info')"
    }
}
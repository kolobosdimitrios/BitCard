package com.example.bitcard.globals

import android.location.Location

class Distance {

    companion object{

        fun toKms(distance: Double?) : Int{
            distance?.let {
                return (distance * 0.001).toInt()
            }

            return 0
        }

        fun calculateDistance(longitude_start: Float, latitude_start: Float, longitude_end: Float, latitude_end: Float): Float{
            val results = FloatArray(1)
            Location.distanceBetween(
                latitude_start.toDouble(),
                longitude_start.toDouble(),
                latitude_end.toDouble(),
                longitude_end.toDouble(),
                results
            )

            return results[0]
        }
    }
}
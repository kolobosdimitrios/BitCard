package com.example.bitcard.globals

class Distance {

    companion object{

        fun toKms(distance: Double?) : Int{
            distance?.let {
                return (distance * 0.001).toInt()
            }

            return 0
        }
    }
}
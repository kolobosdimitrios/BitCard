package com.example.bitcard

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

object BitmapHandler {

    fun decodeImageToBitmap(encodedImage: String): Bitmap {
        val decodedString: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
}
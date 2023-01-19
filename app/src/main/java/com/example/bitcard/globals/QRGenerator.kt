package com.example.bitcard.globals

import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

/**
 * Generates and returns a qr code bitmap from a string
 */
class QR {

    companion object Generator{

        fun generate(content: String, color1: Int, color2: Int) : Bitmap{
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {

                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) color1 else color2)

                }
            }
            return bitmap
        }


    }
}
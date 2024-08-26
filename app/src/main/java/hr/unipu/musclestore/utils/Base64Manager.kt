package hr.unipu.musclestore.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import java.io.ByteArrayOutputStream

object Base64Manager {

    // Convert a Drawable to a Bitmap
    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            // Create a Bitmap with the same dimensions as the Drawable
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            // Create a Canvas to draw the Drawable onto the Bitmap
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    // Encode a Bitmap to Base64 string
    fun encodeBitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        // Compress the bitmap to PNG or JPEG format and convert it to a byte array
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // Decode a Base64 string back to Bitmap
    fun decodeBase64ToBitmap(base64: String): Bitmap? {
        val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
}

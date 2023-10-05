    package com.example.artisticgx

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

//Made by ChatGBT
fun mergeBitmaps(frameBitmap: Bitmap, photoBitmap: Bitmap): Bitmap {
    // Create a mutable copy of the frame bitmap
    val resultBitmap = frameBitmap.copy(Bitmap.Config.ARGB_8888, true)

    // Create a Canvas object to draw on the result bitmap
    val canvas = Canvas(resultBitmap)

    // Calculate the scale factor to fit the photo inside the frame
    val scaleFactorX = resultBitmap.width.toFloat() / photoBitmap.width.toFloat()
    val scaleFactorY = resultBitmap.height.toFloat() / photoBitmap.height.toFloat()

    // Calculate the scaling factor that maintains the aspect ratio
    val scaleFactor = minOf(scaleFactorX, scaleFactorY)

    // Calculate the new dimensions of the photo after scaling
    val newWidth = (photoBitmap.width * scaleFactor).toInt()
    val newHeight = (photoBitmap.height * scaleFactor).toInt()

    // Calculate the position to center the scaled photo inside the frame
    val x = (resultBitmap.width - newWidth) / 2
    val y = (resultBitmap.height - newHeight) / 2

    // Create a RectF for the scaled photo
    val photoRect = RectF(x.toFloat(), y.toFloat(), (x + newWidth).toFloat(), (y + newHeight).toFloat())

    // Draw the scaled photo on the canvas first
    canvas.drawBitmap(photoBitmap, null, photoRect, Paint())

    // Draw the frame on top of the photo
    canvas.drawBitmap(frameBitmap, 0f, 0f, null)

    return resultBitmap
}




fun getBitmapFromDrawable(context: Context, drawableId: Int): Bitmap {
    // Load the drawable into a Bitmap
    val drawable = context.resources.getDrawable(drawableId, null)
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}
// BitmapExtensions.kt
package com.spotfix.android.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.ImageProxy
import org.tensorflow.lite.support.image.TensorImage
import java.io.ByteArrayOutputStream

fun ImageProxy.toBitmap(): Bitmap {
    val yuvImage = YuvImage(this.toNV21ByteArray(), ImageFormat.NV21, width, height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
    val imageBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        .rotate(imageInfo.rotationDegrees.toFloat())
}

private fun ImageProxy.toNV21ByteArray(): ByteArray {
    val yPlane = planes[0]
    val uPlane = planes[1]
    val vPlane = planes[2]

    val yBuffer = yPlane.buffer
    val uBuffer = uPlane.buffer
    val vBuffer = vPlane.buffer

    yBuffer.rewind()
    uBuffer.rewind()
    vBuffer.rewind()

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize * 2)

    // Y
    yBuffer.get(nv21, 0, ySize)

    // UV interleaved
    var i = ySize
    while (vBuffer.hasRemaining() && uBuffer.hasRemaining()) {
        nv21[i++] = vBuffer.get()
        nv21[i++] = uBuffer.get()
    }

    return nv21
}

fun Bitmap.rotate(degrees: Float): Bitmap {
    if (degrees == 0f) return this
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun Bitmap.toTensorImage() = TensorImage.fromBitmap(this)

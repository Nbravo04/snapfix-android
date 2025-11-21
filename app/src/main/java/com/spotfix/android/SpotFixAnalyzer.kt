package com.spotfix.android

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import android.content.Context

class SpotFixAnalyzer(
    private val context: Context,
    private val onResults: (List<Detection>, Int, Int) -> Unit  // Added width and height
) : ImageAnalysis.Analyzer {

    private val detector = EfficientDetDetector(context)

    override fun analyze(image: ImageProxy) {
        try {
            val bitmap = image.toBitmap()
            val results = detector.detect(bitmap)
            onResults(results, bitmap.width, bitmap.height)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            image.close()
        }
    }
}
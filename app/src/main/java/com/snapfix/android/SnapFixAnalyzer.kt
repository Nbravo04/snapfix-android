package com.snapfix.android

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import android.content.Context

class SnapFixAnalyzer(
    private val context: Context,
    private val onResults: (List<Detection>) -> Unit  // Use our Detection data class
) : ImageAnalysis.Analyzer {

    private val detector = EfficientDetDetector(context)

    override fun analyze(image: ImageProxy) {
        try {
            val bitmap = image.toBitmap()
            val results = detector.detect(bitmap)
            onResults(results)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            image.close()
        }
    }
}
package com.snapfix.android

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class SnapFixAnalyzer(
    private val onResult: (String) -> Unit
) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        onResult("Camera LIVE · Weekend 2 → Real AI")
        image.close()
    }
}
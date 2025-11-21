package com.spotfix.android

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Data class to hold captured image and detections
 */
data class CapturedData(
    val bitmap: Bitmap,
    val detections: List<Detection>
)

/**
 * SharedViewModel to share captured data across screens
 */
class SharedViewModel : ViewModel() {
    private val _capturedData = MutableStateFlow<CapturedData?>(null)
    val capturedData: StateFlow<CapturedData?> = _capturedData.asStateFlow()

    fun setCapturedData(bitmap: Bitmap, detections: List<Detection>) {
        _capturedData.value = CapturedData(bitmap, detections)
    }

    fun clearCapturedData() {
        _capturedData.value = null
    }
}

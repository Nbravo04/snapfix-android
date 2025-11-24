package com.spotfix.android.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.spotfix.android.model.CapturedData
import com.spotfix.android.model.Detection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * CameraViewModel to share captured data across screens
 */
class CameraViewModel : ViewModel() {
    private val _capturedData = MutableStateFlow<CapturedData?>(null)
    val capturedData: StateFlow<CapturedData?> = _capturedData.asStateFlow()

    // Trigger for capture from bottom nav
    private val _captureRequested = MutableStateFlow(false)
    val captureRequested: StateFlow<Boolean> = _captureRequested.asStateFlow()

    fun setCapturedData(bitmap: Bitmap, detections: List<Detection>) {
        _capturedData.value = CapturedData(bitmap, detections)
    }

    fun clearCapturedData() {
        _capturedData.value = null
    }

    fun requestCapture() {
        _captureRequested.value = true
    }

    fun clearCaptureRequest() {
        _captureRequested.value = false
    }
}

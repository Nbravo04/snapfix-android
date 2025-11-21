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

    fun setCapturedData(bitmap: Bitmap, detections: List<Detection>) {
        _capturedData.value = CapturedData(bitmap, detections)
    }

    fun clearCapturedData() {
        _capturedData.value = null
    }
}

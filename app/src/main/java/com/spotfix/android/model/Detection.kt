package com.spotfix.android.model

import android.graphics.Bitmap
import android.graphics.RectF

/**
 * Data class representing a single object detection result
 */
data class Detection(
    val boundingBox: RectF,
    val label: String,
    val score: Float
)

/**
 * Data class to hold captured image and detections
 */
data class CapturedData(
    val bitmap: Bitmap,
    val detections: List<Detection>
)

/**
 * Data class for repair advice
 */
data class RepairAdvice(
    val title: String,
    val description: String,
    val steps: List<String>
)

/**
 * Maps detection labels to Material Icons and helpful descriptions
 */
data class DetectionInfo(
    val iconName: String,
    val helpText: String
)

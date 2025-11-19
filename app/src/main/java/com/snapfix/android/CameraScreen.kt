// CameraScreen.kt
package com.snapfix.android

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import android.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas

@Composable
fun CameraScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var detections by remember { mutableStateOf<List<Detection>>(emptyList()) }
    var imageWidth by remember { mutableStateOf(640f) }
    var imageHeight by remember { mutableStateOf(480f) }

    val previewView = remember { PreviewView(context).apply {
        implementationMode = PreviewView.ImplementationMode.PERFORMANCE
        scaleType = PreviewView.ScaleType.FILL_CENTER
    } }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize(),
            update = { view ->
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(view.surfaceProvider)
                    }
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(
                                ContextCompat.getMainExecutor(context),
                                SnapFixAnalyzer(context) { results, width, height ->
                                    detections = results
                                    imageWidth = width.toFloat()
                                    imageHeight = height.toFloat()
                                }
                            )
                        }
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis
                    )
                }, ContextCompat.getMainExecutor(context))
            }
        )

        // Draw bounding boxes
        Canvas(modifier = Modifier.fillMaxSize()) {
            detections.forEach { detection ->
                val box = detection.boundingBox

                // Scale from image coordinates to canvas coordinates
                val scaleX = size.width / imageWidth
                val scaleY = size.height / imageHeight

                val left = box.left * scaleX
                val top = box.top * scaleY
                val width = box.width() * scaleX
                val height = box.height() * scaleY

                drawRect(
                    color = Color.Green,
                    topLeft = Offset(left, top),
                    size = Size(width, height),
                    style = Stroke(8f)
                )

                drawContext.canvas.nativeCanvas.drawText(
                    "${detection.label} ${(detection.score * 100).toInt()}%",
                    left + 16f,
                    top + 48f,
                    Paint().apply {
                        color = android.graphics.Color.GREEN
                        textSize = 48f
                        isAntiAlias = true
                    }
                )
            }
        }

        Text(
            text = "SnapFix v0.2 · EfficientDet · LIVE",
            color = Color.White,
            modifier = Modifier.align(Alignment.BottomCenter).padding(32.dp)
        )
    }
}
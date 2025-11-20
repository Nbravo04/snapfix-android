// CameraScreen.kt
package com.snapfix.android

import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.nativeCanvas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resumeWithException

@Composable
fun CameraScreen(
    onCapture: (Bitmap, List<Detection>) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var detections by remember { mutableStateOf<List<Detection>>(emptyList()) }
    var imageWidth by remember { mutableStateOf(640f) }
    var imageHeight by remember { mutableStateOf(480f) }
    var isCapturing by remember { mutableStateOf(false) }

    val imageCapture = remember { ImageCapture.Builder().build() }
    val detector = remember { EfficientDetDetector(context) }

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
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis,
                        imageCapture
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

        // UI Overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top bar with title
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                )
            ) {
                Text(
                    text = "SnapFix · Live Detection",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom section with SNAP button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Detections counter
                if (detections.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.9f)
                        )
                    ) {
                        Text(
                            text = "${detections.size} object${if (detections.size != 1) "s" else ""} detected",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Big circular SNAP button
                FilledIconButton(
                    onClick = {
                        if (!isCapturing) {
                            isCapturing = true
                            scope.launch {
                                try {
                                    val bitmap = withContext(Dispatchers.IO) {
                                        // Capture high-res frame from ImageCapture
                                        imageCapture.takePicture(ContextCompat.getMainExecutor(context))
                                    }

                                    // Run detection on captured bitmap
                                    val capturedDetections = withContext(Dispatchers.Default) {
                                        detector.detect(bitmap)
                                    }

                                    // Navigate to result screen
                                    onCapture(bitmap, capturedDetections)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                } finally {
                                    isCapturing = false
                                }
                            }
                        }
                    },
                    enabled = !isCapturing,
                    modifier = Modifier
                        .size(80.dp)
                        .border(4.dp, Color.White, CircleShape),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                ) {
                    if (isCapturing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Camera,
                            contentDescription = "Capture",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "SNAP",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Extension to capture image from ImageCapture
 */
private suspend fun ImageCapture.takePicture(executor: java.util.concurrent.Executor): Bitmap {
    return kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
        takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: androidx.camera.core.ImageProxy) {
                val rotationDegrees = image.imageInfo.rotationDegrees

                // Convert ImageProxy to Bitmap (handles JPEG format from ImageCapture)
                val buffer = image.planes[0].buffer
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes)
                val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                // Apply rotation based on image metadata
                val rotatedBitmap = if (rotationDegrees != 0) {
                    val matrix = android.graphics.Matrix().apply {
                        postRotate(rotationDegrees.toFloat())
                    }
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                } else {
                    bitmap
                }

                image.close()
                continuation.resume(rotatedBitmap, null)
            }

            override fun onError(exception: androidx.camera.core.ImageCaptureException) {
                continuation.resumeWithException(exception)
            }
        })
    }
}
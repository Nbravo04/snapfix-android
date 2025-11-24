// CameraScreen.kt
package com.spotfix.android.ui.screen

import android.Manifest
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoLibrary
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
import androidx.compose.ui.text.style.TextAlign
import com.spotfix.android.model.Detection
import com.spotfix.android.utils.EfficientDetDetector
import com.spotfix.android.utils.SpotFixAnalyzer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resumeWithException

@Composable
fun CameraScreen(
    onCapture: (Bitmap, List<Detection>) -> Unit,
    onGalleryClick: () -> Unit,
    onMaintenanceClick: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var detections by remember { mutableStateOf<List<Detection>>(emptyList()) }
    var imageWidth by remember { mutableStateOf(640f) }
    var imageHeight by remember { mutableStateOf(480f) }
    var isCapturing by remember { mutableStateOf(false) }

    // Camera permission state
    var hasCameraPermission by remember {
        mutableStateOf(
            context.checkSelfPermission(Manifest.permission.CAMERA) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }

    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    val imageCapture = remember { ImageCapture.Builder().build() }
    val detector = remember { EfficientDetDetector(context) }

    val previewView = remember { PreviewView(context).apply {
        implementationMode = PreviewView.ImplementationMode.PERFORMANCE
        scaleType = PreviewView.ScaleType.FILL_CENTER
    } }

    // Capture function
    val performCapture: () -> Unit = {
        if (!isCapturing && detections.isNotEmpty()) {
            isCapturing = true
            scope.launch {
                try {
                    val bitmap = withContext(Dispatchers.IO) {
                        imageCapture.takePicture(ContextCompat.getMainExecutor(context))
                    }

                    val capturedDetections = withContext(Dispatchers.Default) {
                        detector.detect(bitmap)
                    }

                    onCapture(bitmap, capturedDetections)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    isCapturing = false
                }
            }
        }
    }

    // Show permission request UI if camera permission not granted
    if (!hasCameraPermission) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // House icon
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    // Title
                    Text(
                        text = "Camera Access Required",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    // Description
                    Text(
                        text = "SpotFix needs camera access to detect objects in real-time and help you diagnose home repair issues.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Grant permission button
                    Button(
                        onClick = {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Grant Camera Permission",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
        return@CameraScreen
    }

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
                                SpotFixAnalyzer(context) { results, width, height ->
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
                    text = "SpotFix · Live Detection",
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

                // Button row: Gallery - SNAP - Maintenance
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Gallery button (left)
                    FilledIconButton(
                        onClick = onGalleryClick,
                        modifier = Modifier.size(56.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Gallery",
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Big circular SNAP button (center)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FilledIconButton(
                            onClick = { performCapture() },
                            enabled = !isCapturing && detections.isNotEmpty(),
                            modifier = Modifier
                                .size(80.dp)
                                .border(4.dp, Color.White, CircleShape),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
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
                            text = if (detections.isEmpty()) "Point at object" else "SNAP",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (detections.isEmpty()) Color.White.copy(alpha = 0.5f) else Color.White
                        )
                    }

                    // Maintenance button (right)
                    FilledIconButton(
                        onClick = onMaintenanceClick,
                        modifier = Modifier.size(56.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Maintenance",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
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
                try {
                    val rotationDegrees = image.imageInfo.rotationDegrees

                    // Convert ImageProxy to Bitmap (handles JPEG format from ImageCapture)
                    val buffer = image.planes[0].buffer
                    buffer.rewind() // Important: rewind buffer before reading
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)
                    val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                    if (bitmap == null) {
                        continuation.resumeWithException(Exception("Failed to decode image"))
                        return
                    }

                    // Apply rotation based on image metadata
                    val rotatedBitmap = if (rotationDegrees != 0) {
                        val matrix = android.graphics.Matrix().apply {
                            postRotate(rotationDegrees.toFloat())
                        }
                        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                    } else {
                        bitmap
                    }

                    continuation.resume(rotatedBitmap, null)
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                } finally {
                    image.close()
                }
            }

            override fun onError(exception: androidx.camera.core.ImageCaptureException) {
                continuation.resumeWithException(exception)
            }
        })
    }
}

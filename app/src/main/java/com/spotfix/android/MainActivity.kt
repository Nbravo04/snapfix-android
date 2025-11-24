package com.spotfix.android

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.spotfix.android.ui.components.BottomNavBar
import com.spotfix.android.ui.theme.SpotFixTheme
import com.spotfix.android.utils.EfficientDetDetector
import com.spotfix.android.utils.uriToBitmap
import com.spotfix.android.viewmodel.CameraViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpotFixTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    val context = LocalContext.current
                    val scope = rememberCoroutineScope()
                    val cameraViewModel: CameraViewModel = viewModel()
                    val detector = remember { EfficientDetDetector(context) }

                    // Gallery picker launcher
                    val photoPickerLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.PickVisualMedia()
                    ) { uri: Uri? ->
                        uri?.let {
                            scope.launch {
                                try {
                                    val bitmap = withContext(Dispatchers.IO) {
                                        context.uriToBitmap(it)
                                    } ?: throw Exception("Failed to load image")

                                    val detections = withContext(Dispatchers.Default) {
                                        detector.detect(bitmap)
                                    }

                                    cameraViewModel.setCapturedData(bitmap, detections)
                                    navController.navigate(Screen.Result.route)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                    // Permission launcher for gallery access
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        if (isGranted) {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        } else {
                            Toast.makeText(context, "Permission required to access gallery", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Show bottom nav on Camera and Maintenance screens
                    val showBottomNav = currentRoute in listOf(
                        Screen.Camera.route,
                        Screen.Maintenance.route
                    )

                    Scaffold(
                        bottomBar = {
                            if (showBottomNav) {
                                BottomNavBar(
                                    currentRoute = currentRoute,
                                    onGalleryClick = {
                                        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            context.checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) ==
                                                android.content.pm.PackageManager.PERMISSION_GRANTED
                                        } else {
                                            context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                                                android.content.pm.PackageManager.PERMISSION_GRANTED
                                        }

                                        if (hasPermission) {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        } else {
                                            permissionLauncher.launch(
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                    Manifest.permission.READ_MEDIA_IMAGES
                                                } else {
                                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                                }
                                            )
                                        }
                                    },
                                    onCameraClick = {
                                        if (currentRoute != Screen.Camera.route) {
                                            navController.navigate(Screen.Camera.route) {
                                                popUpTo(Screen.Camera.route) { inclusive = true }
                                            }
                                        }
                                    },
                                    onMaintenanceClick = {
                                        if (currentRoute != Screen.Maintenance.route) {
                                            navController.navigate(Screen.Maintenance.route) {
                                                popUpTo(Screen.Camera.route)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    ) { paddingValues ->
                        SpotFixNavHost(
                            navController = navController,
                            cameraViewModel = cameraViewModel,
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                }
            }
        }
    }
}
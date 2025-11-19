package com.snapfix.android

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    viewModel: SharedViewModel,
    onBack: () -> Unit,
    onGetAdvice: () -> Unit
) {
    val context = LocalContext.current
    val capturedData by viewModel.capturedData.collectAsState()

    capturedData?.let { data ->
        val annotatedBitmap = remember(data) {
            data.bitmap.drawDetections(data.detections)
        }

        // Sort detections by confidence (highest first)
        val sortedDetections = remember(data) {
            data.detections.sortedByDescending { it.score }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detection Results") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            context.shareBitmap(annotatedBitmap, "Share SnapFix Result")
                        }) {
                            Icon(Icons.Default.Share, "Share")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            floatingActionButton = {
                if (sortedDetections.isNotEmpty()) {
                    ExtendedFloatingActionButton(
                        onClick = onGetAdvice,
                        icon = { Icon(Icons.Default.Lightbulb, "Advice") },
                        text = { Text("Get Advice") },
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Full-screen captured image with bounding boxes
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(
                                annotatedBitmap.width.toFloat() / annotatedBitmap.height.toFloat()
                            )
                    ) {
                        Image(
                            bitmap = annotatedBitmap.asImageBitmap(),
                            contentDescription = "Captured image with detections",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                // Header for detection list
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${sortedDetections.size} Detection${if (sortedDetections.size != 1) "s" else ""} Found",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // List of detections with icons and helpful text
                if (sortedDetections.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No objects detected",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Try capturing a different scene",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(sortedDetections) { detection ->
                        DetectionCard(detection = detection)
                    }
                }

                // Bottom spacing for FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    } ?: run {
        // Fallback if no data
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("No capture data available")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBack) {
                    Text("Go Back")
                }
            }
        }
    }
}

@Composable
fun DetectionCard(detection: Detection) {
    val detectionInfo = getDetectionInfo(detection.label)
    val confidencePercent = (detection.score * 100).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon based on detection type
            Surface(
                color = when {
                    detection.score >= 0.8f -> MaterialTheme.colorScheme.primaryContainer
                    detection.score >= 0.6f -> MaterialTheme.colorScheme.tertiaryContainer
                    else -> MaterialTheme.colorScheme.errorContainer
                },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = getIconForDetection(detectionInfo.iconName),
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = when {
                        detection.score >= 0.8f -> MaterialTheme.colorScheme.onPrimaryContainer
                        detection.score >= 0.6f -> MaterialTheme.colorScheme.onTertiaryContainer
                        else -> MaterialTheme.colorScheme.onErrorContainer
                    }
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Detection info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = detection.label.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Confidence badge
                    Surface(
                        color = when {
                            detection.score >= 0.8f -> MaterialTheme.colorScheme.primary
                            detection.score >= 0.6f -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.error
                        },
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "$confidencePercent%",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = detectionInfo.helpText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Maps icon name to Material Icon
 */
@Composable
fun getIconForDetection(iconName: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (iconName) {
        "person" -> Icons.Default.Person
        "pedal_bike" -> Icons.Default.DirectionsBike
        "directions_car" -> Icons.Default.DirectionsCar
        "two_wheeler" -> Icons.Default.TwoWheeler
        "flight" -> Icons.Default.Flight
        "directions_boat" -> Icons.Default.DirectionsBoat
        "traffic" -> Icons.Default.Traffic
        "local_fire_department" -> Icons.Default.LocalFireDepartment
        "stop" -> Icons.Default.Stop
        "deck" -> Icons.Default.Deck
        "attractions" -> Icons.Default.Attractions
        "pets" -> Icons.Default.Pets
        "backpack" -> Icons.Default.Backpack
        "umbrella" -> Icons.Default.Umbrella
        "checkroom" -> Icons.Default.Checkroom
        "sports_soccer" -> Icons.Default.SportsSoccer
        "skateboarding" -> Icons.Default.Skateboarding
        "sports_baseball" -> Icons.Default.SportsBaseball
        "local_cafe" -> Icons.Default.LocalCafe
        "restaurant" -> Icons.Default.Restaurant
        "chair" -> Icons.Default.Chair
        "local_florist" -> Icons.Default.LocalFlorist
        "bed" -> Icons.Default.Bed
        "table_restaurant" -> Icons.Default.TableRestaurant
        "wc" -> Icons.Default.Wc
        "tv" -> Icons.Default.Tv
        "keyboard" -> Icons.Default.Keyboard
        "settings_remote" -> Icons.Default.SettingsRemote
        "smartphone" -> Icons.Default.Smartphone
        "microwave" -> Icons.Default.Microwave
        "kitchen" -> Icons.Default.Kitchen
        "countertops" -> Icons.Default.Countertops
        "menu_book" -> Icons.Default.MenuBook
        "schedule" -> Icons.Default.Schedule
        "content_cut" -> Icons.Default.ContentCut
        "cruelty_free" -> Icons.Default.CrueltyFree
        "bathroom" -> Icons.Default.Bathroom
        else -> Icons.Default.Build
    }
}

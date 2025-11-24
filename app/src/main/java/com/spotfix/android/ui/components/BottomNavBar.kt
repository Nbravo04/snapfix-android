package com.spotfix.android.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Gallery : BottomNavItem("gallery", Icons.Default.PhotoLibrary, "Gallery")
    object Camera : BottomNavItem("camera", Icons.Default.CameraAlt, "Camera")
    object Maintenance : BottomNavItem("maintenance", Icons.Default.Build, "Maintenance")
}

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onMaintenanceClick: () -> Unit
) {
    val items = listOf(
        BottomNavItem.Gallery,
        BottomNavItem.Camera,
        BottomNavItem.Maintenance
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    when (item) {
                        BottomNavItem.Gallery -> onGalleryClick()
                        BottomNavItem.Camera -> onCameraClick()
                        BottomNavItem.Maintenance -> onMaintenanceClick()
                    }
                }
            )
        }
    }
}

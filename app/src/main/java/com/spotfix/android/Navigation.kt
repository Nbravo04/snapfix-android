package com.spotfix.android

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.spotfix.android.model.Detection
import com.spotfix.android.ui.screen.AdviceScreen
import com.spotfix.android.ui.screen.CameraScreen
import com.spotfix.android.ui.screen.MaintenanceScreen
import com.spotfix.android.ui.screen.ResultScreen
import com.spotfix.android.ui.screen.SplashScreen
import com.spotfix.android.viewmodel.CameraViewModel

/**
 * Navigation routes for SpotFix app
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Camera : Screen("camera")
    object Result : Screen("result")
    object Advice : Screen("advice")
    object Maintenance : Screen("maintenance")
}

/**
 * Main navigation host for SpotFix
 */
@Composable
fun SpotFixNavHost(
    navController: NavHostController,
    cameraViewModel: CameraViewModel = viewModel(),
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    navController.navigate(Screen.Camera.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Camera.route) {
            CameraScreen(
                onCapture = { bitmap, detections ->
                    cameraViewModel.setCapturedData(bitmap, detections)
                    navController.navigate(Screen.Result.route)
                }
            )
        }

        composable(Screen.Result.route) {
            ResultScreen(
                viewModel = cameraViewModel,
                onBack = { navController.popBackStack() },
                onGetAdvice = { navController.navigate(Screen.Advice.route) }
            )
        }

        composable(Screen.Advice.route) {
            AdviceScreen(
                viewModel = cameraViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Maintenance.route) {
            MaintenanceScreen()
        }
    }
}

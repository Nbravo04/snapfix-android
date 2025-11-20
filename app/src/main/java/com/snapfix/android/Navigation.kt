package com.snapfix.android

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

/**
 * Navigation routes for SnapFix app
 */
sealed class Screen(val route: String) {
    object Camera : Screen("camera")
    object Result : Screen("result")
    object Advice : Screen("advice")
}

/**
 * Main navigation host for SnapFix
 */
@Composable
fun SnapFixNavHost(
    navController: NavHostController,
    sharedViewModel: SharedViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Camera.route
    ) {
        composable(Screen.Camera.route) {
            CameraScreen(
                onCapture = { bitmap, detections ->
                    sharedViewModel.setCapturedData(bitmap, detections)
                    navController.navigate(Screen.Result.route)
                }
            )
        }

        composable(Screen.Result.route) {
            ResultScreen(
                viewModel = sharedViewModel,
                onBack = { navController.popBackStack() },
                onGetAdvice = { navController.navigate(Screen.Advice.route) }
            )
        }

        composable(Screen.Advice.route) {
            AdviceScreen(
                viewModel = sharedViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

package com.snapfix.android

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.snapfix.android.ui.theme.SnapFixTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "splash_alpha"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2000)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .alpha(alphaAnim.value)
                .padding(32.dp)
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.snapfix_logo),
                contentDescription = "SnapFix Logo",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // App name with modern styling
            Text(
                text = "Snap",
                fontSize = 42.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFF1976D2),
                letterSpacing = 2.sp
            )
            Text(
                text = "Fix",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6F00),
                letterSpacing = 2.sp,
                modifier = Modifier.offset(y = (-8).dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tagline
            Text(
                text = "Point, Snap, Fix",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF546E7A),
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun SplashScreenPreviewLight() {
    SnapFixTheme(darkTheme = false) {
        SplashScreen(onSplashComplete = {})
    }
}

@Preview(name = "Dark Mode", showBackground = true)
@Composable
private fun SplashScreenPreviewDark() {
    SnapFixTheme(darkTheme = true) {
        SplashScreen(onSplashComplete = {})
    }
}

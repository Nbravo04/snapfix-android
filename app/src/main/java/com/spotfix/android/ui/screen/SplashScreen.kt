package com.spotfix.android.ui.screen

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
import com.spotfix.android.R
import com.spotfix.android.ui.theme.SpotFixTheme
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
                painter = painterResource(id = R.drawable.spotfix_logo),
                contentDescription = "SpotFix Logo",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // App name with modern styling
            Text(
                text = "Spot",
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
                text = "Spot, Scan, Fix",
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
    SpotFixTheme(darkTheme = false) {
        // Static preview without animation
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.spotfix_logo),
                    contentDescription = "SpotFix Logo",
                    modifier = Modifier.size(200.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Spot",
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

                Text(
                    text = "Spot, Scan, Fix",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF546E7A),
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(name = "Dark Mode", showBackground = true)
@Composable
private fun SplashScreenPreviewDark() {
    SpotFixTheme(darkTheme = true) {
        // Static preview without animation - dark theme
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.spotfix_logo),
                    contentDescription = "SpotFix Logo",
                    modifier = Modifier.size(200.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Spot",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Light,
                    color = Color(0xFF90CAF9),
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Fix",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFB74D),
                    letterSpacing = 2.sp,
                    modifier = Modifier.offset(y = (-8).dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Spot, Scan, Fix",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFB0BEC5),
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

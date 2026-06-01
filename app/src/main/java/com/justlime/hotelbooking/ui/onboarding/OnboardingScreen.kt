package com.justlime.hotelbooking.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.justlime.hotelbooking.ui.common.component.HotelButton

@Composable
fun OnboardingScreen(
    onGetStartedClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/13/a9/62/d0/rajasthan-palace-hotel.jpg?w=900&h=1000&s=1",
            contentDescription = "Welcome Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.4f),
                            Color.Black.copy(alpha = 0.9f)
                        ),
                        startY = 500f // Starts the gradient halfway down the screen
                    )
                )
        )

        // 3. Text and Button Content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            Text(
                text = "Experience the\nRoyalty of Rajasthan",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Book luxury heritage hotels, majestic palaces, and desert resorts for an unforgettable stay.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White.copy(alpha = 0.85f)
                )
            )

            Spacer(modifier = Modifier.height(40.dp))
            HotelButton(
                text = "Get Started",
                modifier = Modifier.fillMaxWidth(),
                onClick = onGetStartedClick
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen(
        onGetStartedClick = {}
    )
}

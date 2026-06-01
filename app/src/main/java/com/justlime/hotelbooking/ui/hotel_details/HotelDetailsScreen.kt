package com.justlime.hotelbooking.ui.hotel_details

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.justlime.hotelbooking.data.model.Hotel
import com.justlime.hotelbooking.ui.hotel_details.state.HotelDetailsUiState
import com.justlime.hotelbooking.ui.theme.HotelBookingTheme
import com.justlime.hotelbooking.utils.mockHotels

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailsScreen(
    uiState: HotelDetailsUiState,
    scrollState: ScrollState,
    onNavigateBack: () -> Unit,
    onBookNowClick: (String) -> Unit,
    onToggleFavorite: (String, Boolean) -> Unit
) {

    Scaffold(
        bottomBar = {
            if (uiState is HotelDetailsUiState.Success) {
                val hotel = uiState.hotel
                BookingBottomBar(hotel, onBookNowClick)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is HotelDetailsUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is HotelDetailsUiState.Error -> {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(uiState.message, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = onNavigateBack) { Text("Go Back") }
                    }
                }

                is HotelDetailsUiState.Success -> {
                    val hotel = uiState.hotel
                    Box(modifier = Modifier.fillMaxSize()) {
                        HotelDetailsContent(hotel, scrollState, onNavigateBack) {
                            onToggleFavorite(hotel.id, it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailsTopBar(
    hotel: Hotel,
    onNavigateBack: () -> Unit,
    onToggleFavorite: (Boolean) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.3f), shape = CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // Hotel Name
            Text(
                text = hotel.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                maxLines = 1
            )

            // Favorite Button
            IconButton(
                onClick = { onToggleFavorite(!hotel.isFavorite) },
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.3f), shape = CircleShape)
            ) {
                Icon(
                    imageVector = if (hotel.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (hotel.isFavorite) Color.Red else Color.White
                )
            }
        }
    }
}

@Composable
fun BookingBottomBar(hotel: Hotel, onBookNowClick: (String) -> Unit) {
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 16.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Price per night",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "₹${hotel.pricePerNight.toInt()}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Button(
                onClick = { onBookNowClick(hotel.id) },
                modifier = Modifier
                    .height(56.dp)
                    .width(180.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("Book Now", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun HotelDetailsContent(
    hotel: Hotel,
    scrollState: ScrollState,
    onNavigateBack: () -> Unit,
    onToggleFavorite: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            ImageHeader(hotel)
            DetailsTopBar(
                hotel = hotel,
                onNavigateBack = onNavigateBack,
                onToggleFavorite = onToggleFavorite
            )
        }
        InfoSection(hotel)
    }
}


@Composable
private fun ImageHeader(hotel: Hotel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        AsyncImage(
            model = hotel.imageUrl,
            contentDescription = hotel.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color.Yellow,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${hotel.rating}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun InfoSection(hotel: Hotel) {
    Surface(
        modifier = Modifier
            .offset(y = (-30).dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Spacer(Modifier.height(8.dp))
            // Location section
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = hotel.location,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider(modifier = Modifier.alpha(0.1f))
            Spacer(Modifier.height(24.dp))

            // Amenities / Quick Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                QuickInfoItem(title = "Rooms", subtitle = "${hotel.availableRooms} Left")
                QuickInfoItem(title = "WiFi", subtitle = "Free")
                QuickInfoItem(
                    title = "Meal",
                    subtitle = if (hotel.mealPlans.isNotEmpty()) "Available" else "Unavailable"
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = "About this hotel",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = hotel.description,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Meal Plans Available",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            hotel.mealPlans.forEach { plan ->
                Surface(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = plan,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(Modifier.height(100.dp)) // Padding for bottom bar
        }
    }
}

@Composable
fun QuickInfoItem(title: String, subtitle: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(subtitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun BookingBottomBarPreview() {
    HotelBookingTheme {
        BookingBottomBar(
            hotel = mockHotels[0],
            onBookNowClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QuickInfoItemPreview() {
    HotelBookingTheme {
        QuickInfoItem(
            title = "Rooms",
            subtitle = "5 Left"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsTopBarPreview() {
    HotelBookingTheme {
        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Gray)
            )
            DetailsTopBar(
                hotel = mockHotels[0],
                onNavigateBack = {},
                onToggleFavorite = {}
            )
        }
    }
}


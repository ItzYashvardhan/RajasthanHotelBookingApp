package com.justlime.hotelbooking.ui.hotel_booking

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.justlime.hotelbooking.data.model.MealPrices
import com.justlime.hotelbooking.ui.hotel_booking.state.BookingState
import com.justlime.hotelbooking.ui.hotel_details.state.HotelDetailsUiState
import com.justlime.hotelbooking.ui.theme.HotelBookingTheme
import com.justlime.hotelbooking.utils.mockHotels

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelBookingScreen(
    uiState: HotelDetailsUiState,
    bookingState: BookingState,
    infoDialogPlan: String?,
    onNavigateBack: () -> Unit,
    onBookNowClick: (Double) -> Unit,
    onRoomsChanged: (Int, Int) -> Unit,
    onNightsChanged: (Int) -> Unit,
    onMealPlanSelected: (String, Double) -> Unit,
    onInfoClick: (String) -> Unit,
    onDismissDialog: () -> Unit
) {


    val availableRooms = (uiState as? HotelDetailsUiState.Success)?.hotel?.availableRooms ?: 0
    val isValid =
        bookingState.rooms > 0 && bookingState.nights > 0 && bookingState.rooms <= availableRooms
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Complete Your Booking", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            if (uiState is HotelDetailsUiState.Success) {
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
                                text = "Total Amount",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "₹${bookingState.grandTotal.toInt()}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Button(
                            onClick = { onBookNowClick(bookingState.grandTotal) },
                            enabled = isValid,
                            modifier = Modifier
                                .height(56.dp)
                                .width(200.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text("Confirm and Pay", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
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
                            .padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            uiState.message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onNavigateBack) { Text("Go Back") }
                    }
                }

                is HotelDetailsUiState.Success -> {
                    HotelBookingContent(
                        hotelName = uiState.hotel.name,
                        hotelLocation = uiState.hotel.location,
                        availableRooms = uiState.hotel.availableRooms,
                        mealPrices = uiState.hotel.mealPrices,
                        bookingState = bookingState,
                        // Pass the lambdas directly down
                        onRoomsChanged = { onRoomsChanged(it, uiState.hotel.availableRooms) },
                        onNightsChanged = onNightsChanged,
                        onMealPlanSelected = onMealPlanSelected,
                        availableMealPlans = uiState.hotel.mealPlans,
                        onInfoClick = onInfoClick
                    )
                }
            }
        }
    }
    if (infoDialogPlan != null) {
        val description = when (infoDialogPlan) {
            "All Inclusive" -> "Includes breakfast, lunch, dinner, and selected beverages throughout the day."
            "Breakfast + Dinner" -> "Includes a morning breakfast buffet and an evening dinner."
            "Breakfast" -> "Includes a complimentary morning breakfast."
            else -> "No meals are included with this selection."
        }

        AlertDialog(
            onDismissRequest = onDismissDialog,
            title = { Text(text = "$infoDialogPlan Details") },
            text = { Text(text = description) },
            confirmButton = {
                TextButton(onClick = onDismissDialog) {
                    Text("Got it")
                }
            }
        )
    }
}

@Composable
fun HotelBookingContent(
    hotelName: String,
    hotelLocation: String,
    availableRooms: Int,
    availableMealPlans: List<String>,
    mealPrices: MealPrices,
    bookingState: BookingState,
    onRoomsChanged: (Int) -> Unit,
    onNightsChanged: (Int) -> Unit,
    onMealPlanSelected: (String, Double) -> Unit,
    onInfoClick: (String) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        HotelHeaderCard(hotelName, hotelLocation)

        ConfigurationSection(bookingState, onRoomsChanged, availableRooms, onNightsChanged)
        Spacer(Modifier.height(8.dp))

        MealPlanSection(
            bookingState,
            onMealPlanSelected,
            mealPrices,
            availableMealPlans,
            onInfoClick
        )
        Spacer(Modifier.height(32.dp))

        PriceSummary(bookingState)
        Spacer(Modifier.height(100.dp)) // Added spacing for bottom bar
    }
}

@Composable
private fun PriceSummary(state: BookingState) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "Price Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))

            SummaryRow(
                "Room Stay (${state.rooms} Rooms x ${state.nights} Nights)",
                state.roomCost
            )
            if (state.mealCost > 0) {
                SummaryRow("Meal Plan (${state.selectedMealName})", state.mealCost)
            }
            SummaryRow("GST & Service Tax (18%)", state.tax)

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Total Amount",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    "₹${state.grandTotal.toInt()}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ConfigurationSection(
    bookingState: BookingState,
    onRoomsChanged: (Int) -> Unit,
    availableRooms: Int,
    onNightsChanged: (Int) -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Booking Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 1. Rooms Row
            BookingSelectorRow(
                icon = Icons.Outlined.MeetingRoom,
                label = "Rooms",
                value = bookingState.rooms,
                onDecrease = { onRoomsChanged(bookingState.rooms - 1) },
                onIncrease = { onRoomsChanged(bookingState.rooms + 1) },
                isAtLimit = bookingState.rooms >= availableRooms
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )

            BookingSelectorRow(
                icon = Icons.Outlined.Event,
                label = "Nights",
                value = bookingState.nights,
                onDecrease = { onNightsChanged(bookingState.nights - 1) },
                onIncrease = { onNightsChanged(bookingState.nights + 1) }
            )

            if (bookingState.rooms >= availableRooms) {
                Text(
                    text = "Only $availableRooms rooms left at this price.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun MealPlanSection(
    bookingState: BookingState,
    onMealPlanSelected: (String, Double) -> Unit,
    mealPrices: MealPrices,
    availableMealPlans: List<String>,
    onInfoClick: (String) -> Unit
) {
    Text(
        "Select Meal Plan",
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )

    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        availableMealPlans.forEach { plan ->
            val price = when (plan) {
                "No Meal" -> 0.0
                "Breakfast" -> mealPrices.breakfast.toDouble()
                "Breakfast + Dinner" -> mealPrices.breakfastPlusDinner.toDouble()
                "All Inclusive" -> mealPrices.breakfastPlusDinner.toDouble()
                else -> 0.0
            }
            MealPlanCard(
                name = plan,
                price = price,
                selectedName = bookingState.selectedMealName,
                onSelect = onMealPlanSelected,
                onInfoClick = onInfoClick
            )
        }
    }
}

@Composable
private fun HotelHeaderCard(hotelName: String, hotelLocation: String) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                alpha = 0.3f
            )
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                hotelName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                hotelLocation,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BookingSelectorRow(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    isAtLimit: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Icon and Label
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Capsule
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            // Decrease Button
            IconButton(
                onClick = onDecrease,
                enabled = value > 1,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease",
                    tint = if (value > 1) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.3f
                    )
                )
            }

            // Value Text
            Text(
                text = "$value",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // Increase Button
            IconButton(
                onClick = onIncrease,
                enabled = !isAtLimit,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase",
                    tint = if (!isAtLimit) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.3f
                    )
                )
            }
        }
    }
}

@Composable
fun MealPlanCard(
    name: String,
    price: Double,
    selectedName: String,
    onSelect: (String, Double) -> Unit,
    onInfoClick: (String) -> Unit // 1. Add the stateless click listener
) {
    val isSelected = name == selectedName
    val borderAlpha by animateColorAsState(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(name, price) },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderAlpha),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = isSelected, onClick = { onSelect(name, price) })

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
                Text(
                    if (price == 0.0) "Included with stay" else "+₹${price.toInt()} per person",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 2. Add the trailing Info Icon
            IconButton(
                onClick = { onInfoClick(name) },
                modifier = Modifier.size(32.dp) // Slightly smaller hit target so it doesn't crowd the text
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Information about $name",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, amount: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            "₹${amount.toInt()}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HotelBookingContentPreview() {
    val hotel = mockHotels[0]
    HotelBookingTheme {
        HotelBookingContent(
            hotelName = hotel.name,
            hotelLocation = hotel.location,
            availableRooms = hotel.availableRooms,
            mealPrices = hotel.mealPrices,
            bookingState = BookingState(
                rooms = 1,
                nights = 2,
                selectedMealName = "No Meal",
                selectedMealPrice = 0.0
            ),
            onRoomsChanged = {},
            onNightsChanged = {},
            onMealPlanSelected = { _, _ -> },
            availableMealPlans = mutableListOf("No Meal", "Breakfast", "Breakfast + Dinner"),
            onInfoClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BookingSelectorCardPreview() {
    HotelBookingTheme {
        BookingSelectorRow(
            icon = Icons.Outlined.MeetingRoom,
            label = "Rooms",
            value = 2,
            onDecrease = {},
            onIncrease = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MealPlanCardPreview() {
    HotelBookingTheme {
        MealPlanCard(
            name = "Breakfast",
            price = 500.0,
            selectedName = "Breakfast",
            onSelect = { _, _ -> },
            {}
        )
    }
}

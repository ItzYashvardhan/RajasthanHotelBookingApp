package com.justlime.hotelbooking.ui.hotel_booking.state

data class BookingState(
    val rooms: Int = 1,
    val nights: Int = 1,
    val hotelPricePerNight: Double = 0.0,
    val selectedMealName: String = "No Meal",
    val selectedMealPrice: Double = 0.0,
) {
    val roomCost: Double get() = hotelPricePerNight * rooms * nights
    val mealCost: Double get() = selectedMealPrice * rooms * nights
    val tax: Double get() = (roomCost + mealCost) * 0.18
    val grandTotal: Double get() = roomCost + mealCost + tax
}


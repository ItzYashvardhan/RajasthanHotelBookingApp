package com.justlime.hotelbooking.data.model


data class Hotel(
    val id: String,
    val name: String,
    val location: String,
    val pricePerNight: Float,
    val rating: Float,
    val imageUrl: String,
    val isFavorite: Boolean,
    val description: String,
    val availableRooms: Int,
    val mealPlans: List<String>,
    val mealPrices: MealPrices
)


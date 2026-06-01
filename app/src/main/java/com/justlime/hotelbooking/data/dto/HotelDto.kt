package com.justlime.hotelbooking.data.dto

import com.justlime.hotelbooking.data.model.MealPrices
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HotelDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("location") val location: String,
    @SerialName("price_per_night") val pricePerNight: Float,
    @SerialName("rating") val rating: Float,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("description") val description: String,
    @SerialName("available_rooms") val availableRooms: Int,
    @SerialName("meal_plans") val mealPlans: List<String>,
    @SerialName("meal_prices") val mealPrices: MealPricesDto = MealPricesDto()
)

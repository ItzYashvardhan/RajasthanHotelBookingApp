package com.justlime.hotelbooking.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MealPricesDto(
    @SerialName("breakfast") val breakfast: Float = 0f,
    @SerialName("dinner") val dinner: Float = 0f,
    @SerialName("breakfast_plus_dinner") val breakfastPlusDinner: Float = 0f
)

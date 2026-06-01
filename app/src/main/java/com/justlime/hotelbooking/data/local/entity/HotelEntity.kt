package com.justlime.hotelbooking.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hotels")
data class HotelEntity(
    @PrimaryKey val id: String,
    val name: String,
    val location: String,
    val pricePerNight: Float,
    val rating: Float,
    val imageUrl: String,
    val isFavorite: Boolean = false,
    val description: String,
    val availableRooms: Int,
    val mealPlans: List<String>,
    @Embedded val mealPrices: MealPricesEntity
)
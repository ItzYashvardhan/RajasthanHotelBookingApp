package com.justlime.hotelbooking.utils

import com.justlime.hotelbooking.data.model.Hotel
import com.justlime.hotelbooking.data.model.MealPrices

val mockHotels = listOf(
    Hotel(
        id = "1",
        imageUrl = "https://images.unsplash.com/photo-1566073771259-6a8506099945",
        name = "Grand Luxury Resort",
        location = "Paris, France",
        rating = 4.8f,
        pricePerNight = 250.0f,
        isFavorite = false,
        description = "A luxurious stay in the heart of Paris with stunning views.",
        availableRooms = 5,
        mealPlans = listOf("Breakfast Included", "All Inclusive"),
        mealPrices = MealPrices(breakfast = 200f, dinner = 500f, breakfastPlusDinner = 650f)
    ),
    Hotel(
        id = "2",
        imageUrl = "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4",
        name = "Ocean View Villa",
        location = "Maldives",
        rating = 4.9f,
        pricePerNight = 450.0f,
        isFavorite = false,
        description = "Experience paradise in our overwater villas.",
        availableRooms = 2,
        mealPlans = listOf("Breakfast Included", "Half Board"),
        mealPrices = MealPrices(breakfast = 250f, dinner = 600f, breakfastPlusDinner = 800f)
    ),
    Hotel(
        id = "3",
        imageUrl = "https://images.unsplash.com/photo-1551882547-ff43c63fe78d",
        name = "Mountain Retreat",
        location = "Aspen, USA",
        rating = 4.5f,
        pricePerNight = 180.0f,
        isFavorite = true,
        description = "Cozy cabins with easy access to the ski slopes.",
        availableRooms = 10,
        mealPlans = listOf("Room Only"),
        mealPrices = MealPrices(breakfast = 180f, dinner = 450f, breakfastPlusDinner = 600f)
    )
)
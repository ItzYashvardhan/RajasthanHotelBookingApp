package com.justlime.hotelbooking.data.mapper

import com.justlime.hotelbooking.data.dto.HotelDto
import com.justlime.hotelbooking.data.local.entity.HotelEntity
import com.justlime.hotelbooking.data.model.Hotel

fun HotelDto.toEntity(): HotelEntity {
    return HotelEntity(
        id = id,
        name = name,
        location = location,
        pricePerNight = pricePerNight,
        rating = rating,
        imageUrl = imageUrl,
        isFavorite = false,
        description = description,
        availableRooms = availableRooms,
        mealPlans = mealPlans,
        mealPrices = mealPrices.toEntity()
    )
}

fun HotelDto.toDomainModel(): Hotel {
    return Hotel(
        id = id,
        name = name,
        location = location,
        pricePerNight = pricePerNight,
        rating = rating,
        imageUrl = imageUrl,
        isFavorite = false,
        description = description,
        availableRooms = availableRooms,
        mealPlans = mealPlans,
        mealPrices = mealPrices.toDomainModel()
    )
}

fun HotelEntity.toDomainModel(): Hotel {
    return Hotel(
        id = id,
        name = name,
        location = location,
        pricePerNight = pricePerNight,
        rating = rating,
        imageUrl = imageUrl,
        isFavorite = isFavorite,
        description = description,
        availableRooms = availableRooms,
        mealPlans = mealPlans,
        mealPrices = mealPrices.toDomainModel()
    )
}

fun Hotel.toEntity(): HotelEntity {
    return HotelEntity(
        id = id,
        name = name,
        location = location,
        pricePerNight = pricePerNight,
        rating = rating,
        imageUrl = imageUrl,
        isFavorite = isFavorite,
        description = description,
        availableRooms = availableRooms,
        mealPlans = mealPlans,
        mealPrices = mealPrices.toEntity()
    )
}

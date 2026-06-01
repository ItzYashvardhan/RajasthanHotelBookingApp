package com.justlime.hotelbooking.data.mapper

import com.justlime.hotelbooking.data.dto.MealPricesDto
import com.justlime.hotelbooking.data.local.entity.MealPricesEntity
import com.justlime.hotelbooking.data.model.MealPrices

fun MealPricesDto.toEntity(): MealPricesEntity {
    return MealPricesEntity(
        breakfast = breakfast,
        dinner = dinner,
        breakfastPlusDinner = breakfastPlusDinner
    )
}

fun MealPricesDto.toDomainModel(): MealPrices {
    return MealPrices(
        breakfast = breakfast,
        dinner = dinner,
        breakfastPlusDinner = breakfastPlusDinner
    )
}

fun MealPricesEntity.toDomainModel(): MealPrices {
    return MealPrices(
        breakfast = breakfast,
        dinner = dinner,
        breakfastPlusDinner = breakfastPlusDinner
    )
}

fun MealPrices.toEntity(): MealPricesEntity {
    return MealPricesEntity(
        breakfast = breakfast,
        dinner = dinner,
        breakfastPlusDinner = breakfastPlusDinner
    )
}

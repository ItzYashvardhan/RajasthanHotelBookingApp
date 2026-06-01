package com.justlime.hotelbooking.data.api

import com.justlime.hotelbooking.data.dto.HotelDto
import com.justlime.hotelbooking.data.local.entity.HotelEntity
import com.justlime.hotelbooking.data.model.Hotel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HotelApiService {
    @GET("hotels")
    suspend fun getHotels(
        @Query("_page") page: Int = 1,
        @Query("_limit") limit: Int = 5
    ): List<HotelDto>
}
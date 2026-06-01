package com.justlime.hotelbooking.data.repository

import androidx.paging.PagingData
import com.justlime.hotelbooking.data.model.Hotel
import com.justlime.hotelbooking.ui.hotel_list.SortOrder
import kotlinx.coroutines.flow.Flow

interface HotelRepository {

    fun getPagedHotels(query: String, sortOrder: SortOrder): Flow<PagingData<Hotel>>

    fun getHotelById(id: String): Flow<Hotel?>

    suspend fun toggleFavorite(hotelId: String, isFavorite: Boolean)

    fun getFavoriteHotels(query: String): Flow<PagingData<Hotel>>


    suspend fun refreshHotels()
}
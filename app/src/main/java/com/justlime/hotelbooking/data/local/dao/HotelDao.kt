package com.justlime.hotelbooking.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.justlime.hotelbooking.data.local.entity.HotelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HotelDao {

    @Query("SELECT * FROM hotels WHERE id = :hotelId")
    fun getHotelById(hotelId: String): Flow<HotelEntity?>

    @Query("SELECT * FROM hotels WHERE name LIKE '%' || :query || '%' OR location LIKE '%' || :query || '%' ORDER BY id ASC")
    fun getPagedHotels(query: String): PagingSource<Int, HotelEntity>

    @Query("SELECT * FROM hotels WHERE name LIKE '%' || :query || '%' OR location LIKE '%' || :query || '%' ORDER BY pricePerNight ASC")
    fun getPagedHotelsPriceAsc(query: String): PagingSource<Int, HotelEntity>

    @Query("SELECT * FROM hotels WHERE name LIKE '%' || :query || '%' OR location LIKE '%' || :query || '%' ORDER BY pricePerNight DESC")
    fun getPagedHotelsPriceDesc(query: String): PagingSource<Int, HotelEntity>

    @Query("SELECT * FROM hotels WHERE name LIKE '%' || :query || '%' OR location LIKE '%' || :query || '%' ORDER BY rating DESC")
    fun getPagedHotelsRatingDesc(query: String): PagingSource<Int, HotelEntity>

    @Query("SELECT * FROM hotels WHERE isFavorite = 1 AND (name LIKE '%' || :query || '%' OR location LIKE '%' || :query || '%') ORDER BY id ASC")
    fun getFavoritePagedHotels(query: String): PagingSource<Int, HotelEntity>


    @Query("UPDATE hotels SET isFavorite = :isFavorite WHERE id = :hotelId")
    suspend fun updateFavoriteStatus(hotelId: String, isFavorite: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHotels(hotels: List<HotelEntity>)

    @Query("SELECT id FROM hotels WHERE isFavorite = 1")
    suspend fun getFavoriteHotelIds(): List<String>

    @Query("DELETE FROM hotels")
    suspend fun clearAllHotels()


    @Query("SELECT COUNT(*) FROM hotels")
    suspend fun getHotelCount(): Int
}
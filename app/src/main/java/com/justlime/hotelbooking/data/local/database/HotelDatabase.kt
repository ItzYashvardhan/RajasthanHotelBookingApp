package com.justlime.hotelbooking.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.justlime.hotelbooking.data.local.converter.Converters
import com.justlime.hotelbooking.data.local.dao.HotelDao
import com.justlime.hotelbooking.data.local.entity.HotelEntity

@Database(
    entities = [HotelEntity::class], 
    version = 1, 
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HotelDatabase : RoomDatabase() {
    abstract val hotelDao: HotelDao
}
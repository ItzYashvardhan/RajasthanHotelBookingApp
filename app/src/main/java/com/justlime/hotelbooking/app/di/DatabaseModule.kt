package com.justlime.hotelbooking.app.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.justlime.hotelbooking.data.api.HotelApiService
import com.justlime.hotelbooking.data.local.datasource.OnboardingManager
import com.justlime.hotelbooking.data.local.dao.HotelDao
import com.justlime.hotelbooking.data.local.database.HotelDatabase
import com.justlime.hotelbooking.data.repository.HotelRepository
import com.justlime.hotelbooking.data.repository.impl.HotelRepositoryImpl
import com.justlime.hotelbooking.utils.AppConstant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideOnboardingManager(@ApplicationContext context: Context): OnboardingManager {
        return OnboardingManager(context)
    }

    @Provides
    @Singleton
    fun provideHotelDatabase(app: Application): HotelDatabase {
        return Room.databaseBuilder(
            app,
            HotelDatabase::class.java,
            "hotel_booking_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideHotelDao(db: HotelDatabase): HotelDao {
        return db.hotelDao
    }

    @Provides
    @Singleton
    fun provideHotelApiService(): HotelApiService {
        val contentType = "application/json".toMediaType()
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl(AppConstant.BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(HotelApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideHotelRepository(dao: HotelDao, apiService: HotelApiService): HotelRepository {
        return HotelRepositoryImpl(dao, apiService)
    }
}
package com.justlime.hotelbooking.data.repository.impl

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.justlime.hotelbooking.data.api.HotelApiService
import com.justlime.hotelbooking.data.local.dao.HotelDao
import com.justlime.hotelbooking.data.mapper.toDomainModel
import com.justlime.hotelbooking.data.mapper.toEntity
import com.justlime.hotelbooking.data.model.Hotel
import com.justlime.hotelbooking.data.pager.HotelRemoteMediator
import com.justlime.hotelbooking.data.repository.HotelRepository
import com.justlime.hotelbooking.ui.hotel_list.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HotelRepositoryImpl @Inject constructor(
    private val hotelDao: HotelDao,
    private val apiService: HotelApiService
) : HotelRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getPagedHotels(query: String, sortOrder: SortOrder): Flow<PagingData<Hotel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 5,
                initialLoadSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            remoteMediator = HotelRemoteMediator(apiService, hotelDao),
            pagingSourceFactory = {
                when (sortOrder) {
                    SortOrder.PRICE_LOW_TO_HIGH -> hotelDao.getPagedHotelsPriceAsc(query)
                    SortOrder.PRICE_HIGH_TO_LOW -> hotelDao.getPagedHotelsPriceDesc(query)
                    SortOrder.RATING_HIGH_TO_LOW -> hotelDao.getPagedHotelsRatingDesc(query)
                    else -> hotelDao.getPagedHotels(query)
                }
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomainModel() }
        }
    }


    override fun getHotelById(id: String): Flow<Hotel?> {
        return hotelDao.getHotelById(id).map { entity ->
            entity?.toDomainModel()
        }
    }

    override fun getFavoriteHotels(query: String): Flow<PagingData<Hotel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 5,
                initialLoadSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { hotelDao.getFavoritePagedHotels(query) }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomainModel() }
        }
    }

    override suspend fun refreshHotels() {
        try {
            val remoteHotels = apiService.getHotels()

            // Fetch current favorites BEFORE overwriting
            val favoriteIds = hotelDao.getFavoriteHotelIds()

            val entities = remoteHotels.map { remote ->
                val entity = remote.toEntity()
                entity.copy(isFavorite = favoriteIds.contains(remote.id))
            }
            hotelDao.insertHotels(entities)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun toggleFavorite(hotelId: String, isFavorite: Boolean) {
        hotelDao.updateFavoriteStatus(hotelId, isFavorite)
    }
}

package com.justlime.hotelbooking.data.pager

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.justlime.hotelbooking.data.api.HotelApiService
import com.justlime.hotelbooking.data.local.dao.HotelDao
import com.justlime.hotelbooking.data.local.entity.HotelEntity
import com.justlime.hotelbooking.data.mapper.toEntity
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class HotelRemoteMediator(
    private val api: HotelApiService,
    private val dao: HotelDao
) : RemoteMediator<Int, HotelEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, HotelEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val count = dao.getHotelCount()
                    if (count == 0) 1 else (count / state.config.pageSize) + 1
                }
            }

            val networkHotels = api.getHotels(page = page, limit = state.config.pageSize)
            val endOfPaginationReached = networkHotels.isEmpty()

            if (networkHotels.isNotEmpty()) {
                val favoriteIds = dao.getFavoriteHotelIds().toSet()
                val entities = networkHotels.map { remote ->
                    val entity = remote.toEntity()
                    entity.copy(isFavorite = favoriteIds.contains(remote.id))
                }

                if (loadType == LoadType.REFRESH) {
                    dao.clearAllHotels()
                }
                dao.insertHotels(entities)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}
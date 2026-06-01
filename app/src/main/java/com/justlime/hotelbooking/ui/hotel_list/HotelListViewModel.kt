package com.justlime.hotelbooking.ui.hotel_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.justlime.hotelbooking.data.model.Hotel
import com.justlime.hotelbooking.data.repository.HotelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortOrder(val displayName: String) {
    NONE("Default"),
    PRICE_LOW_TO_HIGH("Price: Low to High"),
    PRICE_HIGH_TO_LOW("Price: High to Low"),
    RATING_HIGH_TO_LOW("Rating: High to Low")
}

@HiltViewModel
class HotelListViewModel @Inject constructor(
    private val repository: HotelRepository
) : ViewModel() {

    private val _isShowingFavorites = MutableStateFlow(false)
    val isShowingFavorites = _isShowingFavorites.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.NONE)
    val sortOrder = _sortOrder.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val pagedHotels: Flow<PagingData<Hotel>> = combine(
        _searchQuery.debounce(1000).distinctUntilChanged(),
        _sortOrder
    ) { query, sort ->
        query to sort
    }.flatMapLatest { (query, sort) ->
        repository.getPagedHotels(query, sort)
    }.cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val favoriteHotels: Flow<PagingData<Hotel>> = _searchQuery
        .debounce(1000)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            repository.getFavoriteHotels(query)
        }.cachedIn(viewModelScope)

    fun onSortOrderChanged(newOrder: SortOrder) {
        _sortOrder.value = newOrder
    }

    fun setShowingFavorites(show: Boolean) {
        _isShowingFavorites.value = show
    }

    fun onFavoriteClick(hotelId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(hotelId, !currentStatus)
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }
}
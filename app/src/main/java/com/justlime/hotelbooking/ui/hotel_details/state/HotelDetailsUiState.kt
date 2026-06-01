package com.justlime.hotelbooking.ui.hotel_details.state

import com.justlime.hotelbooking.data.model.Hotel

sealed interface HotelDetailsUiState {
    object Loading : HotelDetailsUiState
    data class Success(val hotel: Hotel) : HotelDetailsUiState
    data class Error(val message: String) : HotelDetailsUiState
}
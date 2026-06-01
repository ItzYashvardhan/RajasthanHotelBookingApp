package com.justlime.hotelbooking.ui.hotel_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justlime.hotelbooking.data.repository.HotelRepository
import com.justlime.hotelbooking.ui.hotel_booking.state.BookingState
import com.justlime.hotelbooking.ui.hotel_details.state.HotelDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HotelDetailsViewModel @Inject constructor(
    private val repository: HotelRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<HotelDetailsUiState>(HotelDetailsUiState.Loading)
    val uiState: StateFlow<HotelDetailsUiState> = _uiState.asStateFlow()

    private val _bookingState = MutableStateFlow(BookingState())
    val bookingState = _bookingState.asStateFlow()

    private val _infoDialogPlan = MutableStateFlow<String?>(null)
    val infoDialogPlan = _infoDialogPlan.asStateFlow()

    init {
        val hotelId: String? = savedStateHandle.get<String>("hotelId")
        if (hotelId != null) {
            fetchHotelDetails(hotelId)
        } else {
            _uiState.value = HotelDetailsUiState.Error("Hotel not found.")
        }
    }

    private fun fetchHotelDetails(id: String) {
        viewModelScope.launch {
            repository.getHotelById(id).collect { hotel ->
                if (hotel != null) {
                    _uiState.value = HotelDetailsUiState.Success(hotel)
                    val defaultPlan = hotel.mealPlans.firstOrNull() ?: "No Meal"
                    val defaultMealPrice = when (defaultPlan) {
                        "Breakfast" -> hotel.mealPrices.breakfast.toDouble()
                        "Breakfast + Dinner", "All Inclusive" -> hotel.mealPrices.breakfastPlusDinner.toDouble()
                        else -> 0.0
                    }
                    _bookingState.update {
                        it.copy(
                            hotelPricePerNight = hotel.pricePerNight.toDouble(),
                            selectedMealName = defaultPlan,
                            selectedMealPrice = defaultMealPrice
                        )
                    }
                } else {
                    _uiState.value = HotelDetailsUiState.Error("Hotel details not available.")
                }
            }
        }
    }

    fun updateRooms(rooms: Int, maxAvailable: Int) {
        if (rooms in 1..maxAvailable) {
            _bookingState.update { it.copy(rooms = rooms) }
        }
    }

    fun updateNights(nights: Int) {
        if (nights >= 1) {
            _bookingState.update { it.copy(nights = nights) }
        }
    }

    fun selectMealPlan(name: String, price: Double) {
        _bookingState.update { it.copy(selectedMealName = name, selectedMealPrice = price) }
    }

    fun resetBooking() {
        _bookingState.value = BookingState()
    }

    fun setFavorite(hotelId: String, isFavourite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(hotelId, isFavourite)
        }
    }

    // 2. Add the intent functions
    fun showInfoDialog(planName: String) {
        _infoDialogPlan.value = planName
    }

    fun dismissInfoDialog() {
        _infoDialogPlan.value = null
    }
}
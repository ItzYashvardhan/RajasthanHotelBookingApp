package com.justlime.hotelbooking

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justlime.hotelbooking.data.local.datasource.OnboardingManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val onboardingManager: OnboardingManager
) : ViewModel() {

    var isLoading by mutableStateOf(true)
        private set

    var startDestination by mutableStateOf("onboarding")
        private set

    init {
        viewModelScope.launch {
            onboardingManager.hasSeenOnboarding.collect { hasSeen ->
                startDestination = if (hasSeen) "hotel_list" else "onboarding"
                isLoading = false
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            onboardingManager.saveOnboardingState(true)
        }
    }
}
package com.justlime.hotelbooking.data.local.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class OnboardingManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
    }

    val hasSeenOnboarding: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[HAS_SEEN_ONBOARDING] ?: false // Default is false
    }

    suspend fun saveOnboardingState(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[HAS_SEEN_ONBOARDING] = completed
        }
    }

    suspend fun clearOnboardingState() {
        dataStore.edit { preferences ->
            preferences.remove(HAS_SEEN_ONBOARDING)
        }
    }
}

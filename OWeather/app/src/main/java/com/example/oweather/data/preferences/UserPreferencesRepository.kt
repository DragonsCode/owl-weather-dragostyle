package com.example.oweather.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

data class UserPreferences(
    val selectedCityId: Long?
)

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val dataStore = PreferenceDataStoreFactory.create {
        context.preferencesDataStoreFile(DATASTORE_NAME)
    }

    val preferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(androidx.datastore.preferences.core.emptyPreferences())
            } else {
                throw it
            }
        }
        .map { prefs ->
            UserPreferences(
                selectedCityId = prefs[SELECTED_CITY_ID]
            )
        }

    suspend fun setSelectedCityId(cityId: Long?) {
        dataStore.edit { prefs ->
            if (cityId == null) {
                prefs.remove(SELECTED_CITY_ID)
            } else {
                prefs[SELECTED_CITY_ID] = cityId
            }
        }
    }

    private companion object {
        const val DATASTORE_NAME = "user_preferences"
        val SELECTED_CITY_ID = longPreferencesKey("selected_city_id")
    }
}

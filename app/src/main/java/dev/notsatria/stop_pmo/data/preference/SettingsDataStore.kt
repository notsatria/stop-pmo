package dev.notsatria.stop_pmo.data.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.notsatria.stop_pmo.utils.UiMode
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "app_settings"

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

class SettingsDataStore(private val dataStore: DataStore<Preferences>) {
    companion object {
        val UI_MODE = intPreferencesKey("ui_mode")
        val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
        val TIME_FORMAT_24H = booleanPreferencesKey("time_format_24h")
    }

    suspend fun setUiMode(mode: Int) {
        dataStore.edit { it[UI_MODE] = mode }
    }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.edit { it[NOTIFICATION_ENABLED] = enabled }
    }

    suspend fun setTimeFormat24H(enabled: Boolean) {
        dataStore.edit { it[TIME_FORMAT_24H] = enabled }
    }

    val uiModeFlow = dataStore.data.map { it[UI_MODE] ?: UiMode.SYSTEM }

    val notificationEnabledFlow = dataStore.data.map { it[NOTIFICATION_ENABLED] ?: true }

    val timeFormat24HFlow = dataStore.data.map { it[TIME_FORMAT_24H] ?: false }
}
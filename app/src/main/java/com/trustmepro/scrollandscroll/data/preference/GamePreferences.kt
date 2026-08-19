package com.trustmepro.scrollandscroll.data.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.trustmepro.scrollandscroll.data.model.GameStats
import com.trustmepro.scrollandscroll.data.model.SkinType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "scroll_game_prefs")

class GamePreferences(private val context: Context) {

    companion object {
        val KEY_TOTAL_METERS = doublePreferencesKey("total_meters")
        val KEY_TOTAL_SWIPES = longPreferencesKey("total_swipes")
        val KEY_NICKNAME = stringPreferencesKey("nickname")
        val KEY_SELECTED_SKIN = stringPreferencesKey("selected_skin_id")
        val KEY_SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val KEY_HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
        val KEY_UNLOCKED_SKINS = stringSetPreferencesKey("unlocked_skin_ids")
        val KEY_UNLOCKED_BADGES = stringSetPreferencesKey("unlocked_badge_ids")
    }

    val gameStatsFlow: Flow<GameStats> = context.dataStore.data.map { prefs ->
        val totalMeters = prefs[KEY_TOTAL_METERS] ?: 0.0
        val totalSwipes = prefs[KEY_TOTAL_SWIPES] ?: 0L
        val nickname = prefs[KEY_NICKNAME] ?: ""
        val selectedSkin = prefs[KEY_SELECTED_SKIN] ?: SkinType.SCHOOL_CANTEEN.id
        val soundEnabled = prefs[KEY_SOUND_ENABLED] ?: true
        val hapticEnabled = prefs[KEY_HAPTIC_ENABLED] ?: true
        val unlockedSkins = prefs[KEY_UNLOCKED_SKINS] ?: setOf(SkinType.SCHOOL_CANTEEN.id)
        val unlockedBadges = prefs[KEY_UNLOCKED_BADGES] ?: emptySet()

        GameStats(
            totalMeters = totalMeters,
            totalSwipes = totalSwipes,
            nickname = nickname,
            selectedSkinId = selectedSkin,
            isSoundEnabled = soundEnabled,
            isHapticEnabled = hapticEnabled,
            unlockedSkinIds = unlockedSkins,
            unlockedBadgeIds = unlockedBadges
        )
    }

    suspend fun addProgress(metersDelta: Double, swipesDelta: Long) {
        context.dataStore.edit { prefs ->
            val currentMeters = prefs[KEY_TOTAL_METERS] ?: 0.0
            val currentSwipes = prefs[KEY_TOTAL_SWIPES] ?: 0L
            prefs[KEY_TOTAL_METERS] = currentMeters + metersDelta
            prefs[KEY_TOTAL_SWIPES] = currentSwipes + swipesDelta
        }
    }

    suspend fun setNickname(nickname: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NICKNAME] = nickname
        }
    }

    suspend fun setSelectedSkin(skinId: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SELECTED_SKIN] = skinId
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SOUND_ENABLED] = enabled
        }
    }

    suspend fun setHapticEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_HAPTIC_ENABLED] = enabled
        }
    }

    suspend fun unlockSkin(skinId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_UNLOCKED_SKINS] ?: setOf(SkinType.SCHOOL_CANTEEN.id)
            prefs[KEY_UNLOCKED_SKINS] = current + skinId
        }
    }

    suspend fun unlockBadge(badgeId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_UNLOCKED_BADGES] ?: emptySet()
            prefs[KEY_UNLOCKED_BADGES] = current + badgeId
        }
    }
}

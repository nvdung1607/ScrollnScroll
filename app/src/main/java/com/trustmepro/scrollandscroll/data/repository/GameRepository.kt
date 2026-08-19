package com.trustmepro.scrollandscroll.data.repository

import com.trustmepro.scrollandscroll.data.model.BadgeType
import com.trustmepro.scrollandscroll.data.model.GameStats
import com.trustmepro.scrollandscroll.data.model.SkinType
import com.trustmepro.scrollandscroll.data.preference.GamePreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class GameRepository(private val preferences: GamePreferences) {

    val gameStatsFlow: Flow<GameStats> = preferences.gameStatsFlow

    suspend fun addProgress(metersDelta: Double, swipesDelta: Long): NewlyUnlockedResult {
        preferences.addProgress(metersDelta, swipesDelta)
        val currentStats = preferences.gameStatsFlow.first()

        // Check if any new skins or badges need unlocking
        val newlyUnlockedSkins = mutableListOf<SkinType>()
        SkinType.entries.forEach { skin ->
            if (currentStats.totalMeters >= skin.requiredMeters && !currentStats.unlockedSkinIds.contains(skin.id)) {
                preferences.unlockSkin(skin.id)
                newlyUnlockedSkins.add(skin)
            }
        }

        var newlyUnlockedBadge: BadgeType? = null
        BadgeType.entries.forEach { badge ->
            if (currentStats.totalMeters >= badge.requiredMeters && !currentStats.unlockedBadgeIds.contains(badge.id)) {
                preferences.unlockBadge(badge.id)
                newlyUnlockedBadge = badge
            }
        }

        return NewlyUnlockedResult(newlyUnlockedSkins, newlyUnlockedBadge)
    }

    suspend fun setNickname(nickname: String) {
        preferences.setNickname(nickname.trim())
    }

    suspend fun selectSkin(skinId: String) {
        preferences.setSelectedSkin(skinId)
    }

    suspend fun toggleSound(enabled: Boolean) {
        preferences.setSoundEnabled(enabled)
    }

    suspend fun toggleHaptic(enabled: Boolean) {
        preferences.setHapticEnabled(enabled)
    }
}

data class NewlyUnlockedResult(
    val newSkins: List<SkinType>,
    val newBadge: BadgeType?
)

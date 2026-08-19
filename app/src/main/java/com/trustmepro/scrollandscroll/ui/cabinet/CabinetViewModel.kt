package com.trustmepro.scrollandscroll.ui.cabinet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.trustmepro.scrollandscroll.data.model.BadgeType
import com.trustmepro.scrollandscroll.data.model.SkinType
import com.trustmepro.scrollandscroll.data.preference.GamePreferences
import com.trustmepro.scrollandscroll.data.repository.GameRepository
import com.trustmepro.scrollandscroll.util.HapticManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class CabinetTab {
    SKINS,
    BADGES
}

data class CabinetUiState(
    val selectedTab: CabinetTab = CabinetTab.SKINS,
    val selectedSkin: SkinType = SkinType.SCHOOL_CANTEEN,
    val totalMeters: Double = 0.0,
    val unlockedSkinIds: Set<String> = emptySet(),
    val unlockedBadgeIds: Set<String> = emptySet(),
    val selectedBadgeForCertificate: BadgeType? = null
)

class CabinetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository(GamePreferences(application))
    private val hapticManager = HapticManager(application)

    private val _uiState = MutableStateFlow(CabinetUiState())
    val uiState: StateFlow<CabinetUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.gameStatsFlow.collect { stats ->
                _uiState.update { current ->
                    current.copy(
                        selectedSkin = stats.selectedSkin,
                        totalMeters = stats.totalMeters,
                        unlockedSkinIds = SkinType.entries
                            .filter { stats.totalMeters >= it.requiredMeters }
                            .map { it.id }
                            .toSet(),
                        unlockedBadgeIds = stats.unlockedBadgeIds
                    )
                }
            }
        }
    }

    fun selectTab(tab: CabinetTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        hapticManager.tick()
    }

    fun equipSkin(skin: SkinType) {
        viewModelScope.launch {
            repository.selectSkin(skin.id)
            hapticManager.click()
        }
    }

    fun showCertificate(badge: BadgeType) {
        _uiState.update { it.copy(selectedBadgeForCertificate = badge) }
        hapticManager.click()
    }

    fun dismissCertificate() {
        _uiState.update { it.copy(selectedBadgeForCertificate = null) }
    }
}

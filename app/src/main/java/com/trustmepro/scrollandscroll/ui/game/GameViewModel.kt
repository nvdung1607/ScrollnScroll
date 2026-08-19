package com.trustmepro.scrollandscroll.ui.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.trustmepro.scrollandscroll.audio.SoundManager
import com.trustmepro.scrollandscroll.data.model.BadgeType
import com.trustmepro.scrollandscroll.data.model.GameStats
import com.trustmepro.scrollandscroll.data.model.SkinType
import com.trustmepro.scrollandscroll.data.preference.GamePreferences
import com.trustmepro.scrollandscroll.data.repository.GameRepository
import com.trustmepro.scrollandscroll.util.HapticManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = GamePreferences(application)
    private val repository = GameRepository(preferences)
    val soundManager = SoundManager(application)
    val hapticManager = HapticManager(application)

    private val _uiState = MutableStateFlow(GameStats())
    val uiState: StateFlow<GameStats> = _uiState.asStateFlow()

    // Swipe timestamp buffer for SPS calculation (1.0s sliding window)
    private val swipeTimestamps = ArrayDeque<Long>()

    // Memory accumulator for debounced disk persistence
    private var accumulatedMeters = 0.0
    private var accumulatedSwipes = 0L
    private var saveJob: Job? = null

    init {
        // Collect persistent state from DataStore
        viewModelScope.launch {
            repository.gameStatsFlow.collect { stats ->
                _uiState.update { current ->
                    current.copy(
                        totalMeters = stats.totalMeters + accumulatedMeters,
                        totalSwipes = stats.totalSwipes + accumulatedSwipes,
                        nickname = stats.nickname,
                        selectedSkinId = stats.selectedSkinId,
                        isSoundEnabled = stats.isSoundEnabled,
                        isHapticEnabled = stats.isHapticEnabled,
                        unlockedSkinIds = stats.unlockedSkinIds,
                        unlockedBadgeIds = stats.unlockedBadgeIds
                    )
                }
                soundManager.isSoundEnabled = stats.isSoundEnabled
                hapticManager.isHapticEnabled = stats.isHapticEnabled
            }
        }

        // Periodic SPS decay & Overdrive check loop
        viewModelScope.launch {
            while (true) {
                delay(200)
                updateSps()
            }
        }
    }

    /**
     * Process scroll action from Canvas gestures
     * @param rawPixels distance scrolled in pixels
     */
    fun onScroll(rawPixels: Float) {
        if (rawPixels <= 0f) return

        val now = System.currentTimeMillis()
        swipeTimestamps.addLast(now)

        // 1 full swipe screen (~2000px) ~ 1 meter -> 1px = 0.0005m
        val baseMeters = (rawPixels * 0.0005).coerceAtLeast(0.0001)
        val multiplier = if (_uiState.value.isOverdrive) 1.5 else 1.0
        val effectiveMeters = baseMeters * multiplier

        accumulatedMeters += effectiveMeters
        accumulatedSwipes += 1

        _uiState.update { current ->
            current.copy(
                totalMeters = current.totalMeters + effectiveMeters,
                totalSwipes = current.totalSwipes + 1
            )
        }

        // ASMR Sound & Haptic
        val pitch = 0.8f + (_uiState.value.currentSps * 0.08f).coerceAtMost(1.2f)
        soundManager.playRoll(pitch)
        hapticManager.tick()

        updateSps()
        scheduleDebouncedSave()
    }

    private fun updateSps() {
        val now = System.currentTimeMillis()
        while (swipeTimestamps.isNotEmpty() && (now - swipeTimestamps.first()) > 1000L) {
            swipeTimestamps.removeFirst()
        }

        val sps = swipeTimestamps.size.toFloat()
        val wasOverdrive = _uiState.value.isOverdrive
        val isNowOverdrive = sps >= 8.0f

        if (!wasOverdrive && isNowOverdrive) {
            soundManager.playOverdrive()
            hapticManager.heavyPulse()
        }

        _uiState.update { it.copy(currentSps = sps, isOverdrive = isNowOverdrive) }
    }

    private fun scheduleDebouncedSave() {
        if (saveJob?.isActive == true) return
        saveJob = viewModelScope.launch {
            delay(500)
            flushAccumulatedProgress()
        }
    }

    private suspend fun flushAccumulatedProgress() {
        if (accumulatedMeters <= 0.0 && accumulatedSwipes <= 0L) return

        val metersToSave = accumulatedMeters
        val swipesToSave = accumulatedSwipes
        accumulatedMeters = 0.0
        accumulatedSwipes = 0L

        val result = repository.addProgress(metersToSave, swipesToSave)

        if (result.newBadge != null) {
            soundManager.playFanfare()
            hapticManager.heavyPulse()
            _uiState.update { it.copy(newlyUnlockedBadge = result.newBadge) }
        } else if (result.newSkins.isNotEmpty()) {
            soundManager.playPop()
            hapticManager.click()
        }
    }

    fun dismissBadgeDialog() {
        _uiState.update { it.copy(newlyUnlockedBadge = null) }
    }

    fun setNickname(nickname: String) {
        viewModelScope.launch {
            repository.setNickname(nickname)
        }
    }

    fun selectSkin(skinId: String) {
        viewModelScope.launch {
            repository.selectSkin(skinId)
            soundManager.playPop()
            hapticManager.click()
        }
    }

    fun toggleSound() {
        viewModelScope.launch {
            val newState = !_uiState.value.isSoundEnabled
            repository.toggleSound(newState)
            soundManager.playClick()
        }
    }

    fun toggleHaptic() {
        viewModelScope.launch {
            val newState = !_uiState.value.isHapticEnabled
            repository.toggleHaptic(newState)
            if (newState) hapticManager.click()
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}

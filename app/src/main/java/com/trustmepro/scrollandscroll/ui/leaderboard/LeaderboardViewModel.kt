package com.trustmepro.scrollandscroll.ui.leaderboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.trustmepro.scrollandscroll.data.model.CountryRank
import com.trustmepro.scrollandscroll.data.model.LeaderboardUser
import com.trustmepro.scrollandscroll.data.preference.GamePreferences
import com.trustmepro.scrollandscroll.data.repository.GameRepository
import com.trustmepro.scrollandscroll.data.repository.LeaderboardRepository
import com.trustmepro.scrollandscroll.util.HapticManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class LeaderboardTab {
    DAILY,
    ALL_TIME,
    COUNTRY
}

data class LeaderboardUiState(
    val selectedTab: LeaderboardTab = LeaderboardTab.DAILY,
    val dailyUsers: List<LeaderboardUser> = emptyList(),
    val allTimeUsers: List<LeaderboardUser> = emptyList(),
    val countryRanks: List<CountryRank> = emptyList(),
    val searchQuery: String = "",
    val currentUserMeters: Double = 0.0,
    val currentNickname: String = "",
    val isLoading: Boolean = false
) {
    val currentUserDailyRank: LeaderboardUser?
        get() = dailyUsers.find { it.isCurrentUser }

    val currentUserAllTimeRank: LeaderboardUser?
        get() = allTimeUsers.find { it.isCurrentUser }

    val filteredDailyUsers: List<LeaderboardUser>
        get() = if (searchQuery.isBlank()) dailyUsers else dailyUsers.filter { it.nickname.contains(searchQuery, ignoreCase = true) }

    val filteredAllTimeUsers: List<LeaderboardUser>
        get() = if (searchQuery.isBlank()) allTimeUsers else allTimeUsers.filter { it.nickname.contains(searchQuery, ignoreCase = true) }

    val filteredCountryRanks: List<CountryRank>
        get() = if (searchQuery.isBlank()) countryRanks else countryRanks.filter { it.countryName.contains(searchQuery, ignoreCase = true) }
}

class LeaderboardViewModel(application: Application) : AndroidViewModel(application) {

    private val gameRepository = GameRepository(GamePreferences(application))
    private val leaderboardRepository = LeaderboardRepository()
    private val hapticManager = HapticManager(application)

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val stats = gameRepository.gameStatsFlow.first()
            val meters = stats.totalMeters
            val name = stats.nickname

            leaderboardRepository.getDailyLeaderboard(meters, name).collect { daily ->
                _uiState.update { it.copy(dailyUsers = daily, currentUserMeters = meters, currentNickname = name) }
            }

            leaderboardRepository.getAllTimeLeaderboard(meters, name).collect { allTime ->
                _uiState.update { it.copy(allTimeUsers = allTime) }
            }

            leaderboardRepository.getCountryBattleLeaderboard(meters).collect { countries ->
                _uiState.update { it.copy(countryRanks = countries, isLoading = false) }
            }
        }
    }

    fun selectTab(tab: LeaderboardTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        hapticManager.tick()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}

package com.trustmepro.scrollandscroll.data.model

data class LeaderboardUser(
    val rank: Int,
    val userId: String,
    val nickname: String,
    val countryCode: String,
    val countryFlag: String,
    val totalMeters: Double,
    val skinEmoji: String,
    val badgeTitle: String,
    val isCurrentUser: Boolean = false
)

data class CountryRank(
    val rank: Int,
    val countryCode: String,
    val countryName: String,
    val flagEmoji: String,
    val totalMeters: Double,
    val totalPlayers: Long
)

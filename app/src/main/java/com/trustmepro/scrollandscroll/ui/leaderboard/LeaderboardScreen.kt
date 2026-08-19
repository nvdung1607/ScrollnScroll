package com.trustmepro.scrollandscroll.ui.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.trustmepro.scrollandscroll.data.model.CountryRank
import com.trustmepro.scrollandscroll.data.model.LeaderboardUser
import com.trustmepro.scrollandscroll.ui.components.ComicCard
import com.trustmepro.scrollandscroll.ui.components.ComicCircleButton
import com.trustmepro.scrollandscroll.ui.components.ComicCyan
import com.trustmepro.scrollandscroll.ui.components.ComicGold
import com.trustmepro.scrollandscroll.ui.components.ComicGreen
import com.trustmepro.scrollandscroll.ui.components.ComicInkBlack
import com.trustmepro.scrollandscroll.ui.components.ComicOrange
import com.trustmepro.scrollandscroll.ui.components.ComicYellow
import com.trustmepro.scrollandscroll.ui.theme.ComicFontFamily
import java.util.Locale

/**
 * Màn hình Bảng Xếp Hạng Toàn Cầu & Đại Chiến Quốc Gia chuẩn Comic Pop-Art
 */
@Composable
fun LeaderboardScreen(
    onBack: () -> Unit,
    viewModel: LeaderboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF9E6))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // ── 1. Header Top Bar ─────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ComicCircleButton(
                    label = "",
                    containerColor = ComicCyan,
                    onClick = onBack,
                    size = 46.dp
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ComicInkBlack,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "BẢNG XẾP HẠNG",
                        fontFamily = ComicFontFamily,
                        fontSize = 22.sp,
                        color = ComicInkBlack
                    )
                    Text(
                        text = "Đại hội võ lâm cuộn giấy vô tri",
                        style = MaterialTheme.typography.labelSmall,
                        color = ComicInkBlack.copy(alpha = 0.65f)
                    )
                }
            }

            // ── 2. Search Bar ────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = {
                        Text(
                            text = "🔍 Tìm kiếm người chơi / quốc gia...",
                            style = MaterialTheme.typography.bodySmall,
                            color = ComicInkBlack.copy(alpha = 0.5f)
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = ComicInkBlack,
                        unfocusedBorderColor = ComicInkBlack.copy(alpha = 0.4f),
                        focusedTextColor = ComicInkBlack,
                        unfocusedTextColor = ComicInkBlack
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            // ── 3. Tabs Switcher ─────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val currentTab = uiState.selectedTab
                LeaderboardTabPill(
                    title = "🔥 HÔM NAY",
                    isSelected = currentTab == LeaderboardTab.DAILY,
                    selectedColor = ComicYellow,
                    onClick = { viewModel.selectTab(LeaderboardTab.DAILY) },
                    modifier = Modifier.weight(1f)
                )
                LeaderboardTabPill(
                    title = "🌍 TOÀN CẦU",
                    isSelected = currentTab == LeaderboardTab.ALL_TIME,
                    selectedColor = ComicGold,
                    onClick = { viewModel.selectTab(LeaderboardTab.ALL_TIME) },
                    modifier = Modifier.weight(1f)
                )
                LeaderboardTabPill(
                    title = "⚔️ QUỐC GIA",
                    isSelected = currentTab == LeaderboardTab.COUNTRY,
                    selectedColor = ComicOrange,
                    onClick = { viewModel.selectTab(LeaderboardTab.COUNTRY) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(10.dp))

            // ── 4. Main Ranking List ─────────────────────────────────────────
            Box(modifier = Modifier.weight(1f)) {
                when (uiState.selectedTab) {
                    LeaderboardTab.DAILY -> {
                        UserRankingList(users = uiState.filteredDailyUsers)
                    }
                    LeaderboardTab.ALL_TIME -> {
                        UserRankingList(users = uiState.filteredAllTimeUsers)
                    }
                    LeaderboardTab.COUNTRY -> {
                        CountryRankingList(countries = uiState.filteredCountryRanks)
                    }
                }
            }

            // ── 5. Sticky Bottom User Rank Bar ───────────────────────────────
            val myRank = if (uiState.selectedTab == LeaderboardTab.ALL_TIME) {
                uiState.currentUserAllTimeRank
            } else {
                uiState.currentUserDailyRank
            }

            myRank?.let { user ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .shadow(8.dp, RoundedCornerShape(18.dp))
                        .clip(RoundedCornerShape(18.dp))
                        .background(ComicInkBlack)
                        .border(3.dp, ComicYellow, RoundedCornerShape(18.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Badge thứ hạng
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ComicYellow),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#${user.rank}",
                                fontFamily = ComicFontFamily,
                                fontSize = 14.sp,
                                color = ComicInkBlack
                            )
                        }

                        Spacer(Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Hạng của bạn: ${user.nickname}",
                                fontFamily = ComicFontFamily,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Tiếp tục vuốt để bứt phá bảng vàng!",
                                style = MaterialTheme.typography.labelSmall,
                                color = ComicYellow.copy(alpha = 0.85f)
                            )
                        }

                        Text(
                            text = "${String.format(Locale.US, "%,.1f", user.totalMeters)}m",
                            fontFamily = ComicFontFamily,
                            fontSize = 16.sp,
                            color = ComicYellow
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// User Ranking List Composable
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun UserRankingList(users: List<LeaderboardUser>) {
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(users, key = { it.userId }) { user ->
            UserRankCard(user = user)
        }
    }
}

@Composable
private fun UserRankCard(user: LeaderboardUser) {
    val isTop1 = user.rank == 1
    val isTop2 = user.rank == 2
    val isTop3 = user.rank == 3

    val cardBg = when {
        user.isCurrentUser -> Color(0xFFE8F5E9)
        isTop1 -> Color(0xFFFFFDE7)
        isTop2 -> Color(0xFFF5F5F5)
        isTop3 -> Color(0xFFFFF3E0)
        else -> Color.White
    }

    val rankBadgeBg = when {
        isTop1 -> ComicGold
        isTop2 -> Color(0xFFB0BEC5)
        isTop3 -> Color(0xFFFFB74D)
        else -> Color(0xFFEEEEEE)
    }

    ComicCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = cardBg
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Number / Crown
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(rankBadgeBg)
                    .border(2.dp, ComicInkBlack, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isTop1) {
                    Text(text = "👑", fontSize = 20.sp)
                } else {
                    Text(
                        text = "#${user.rank}",
                        fontFamily = ComicFontFamily,
                        fontSize = 13.sp,
                        color = ComicInkBlack
                    )
                }
            }

            Spacer(Modifier.width(10.dp))

            // Flag + Avatar
            Text(text = user.countryFlag, fontSize = 22.sp)
            Spacer(Modifier.width(6.dp))
            Text(text = user.skinEmoji, fontSize = 20.sp)

            Spacer(Modifier.width(8.dp))

            // Nickname + Tag
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.nickname,
                    fontFamily = ComicFontFamily,
                    fontSize = 14.sp,
                    color = ComicInkBlack
                )
                Text(
                    text = user.badgeTitle,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.5.sp,
                    color = ComicInkBlack.copy(alpha = 0.6f)
                )
            }

            // Meters
            Text(
                text = "${String.format(Locale.US, "%,.0f", user.totalMeters)} m",
                fontFamily = ComicFontFamily,
                fontSize = 15.sp,
                color = if (isTop1) Color(0xFFFF5722) else ComicInkBlack
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Country Battle Ranking List Composable
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CountryRankingList(countries: List<CountryRank>) {
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(countries, key = { it.countryCode }) { country ->
            CountryRankCard(country = country)
        }
    }
}

@Composable
private fun CountryRankCard(country: CountryRank) {
    val isTop1 = country.rank == 1

    ComicCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = if (isTop1) Color(0xFFFFFDE7) else Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Number
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isTop1) ComicGold else Color(0xFFEEEEEE))
                    .border(2.dp, ComicInkBlack, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${country.rank}",
                    fontFamily = ComicFontFamily,
                    fontSize = 13.sp,
                    color = ComicInkBlack
                )
            }

            Spacer(Modifier.width(12.dp))

            // Country Flag
            Text(text = country.flagEmoji, fontSize = 28.sp)

            Spacer(Modifier.width(10.dp))

            // Country Name & Players
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = country.countryName,
                    fontFamily = ComicFontFamily,
                    fontSize = 15.sp,
                    color = ComicInkBlack
                )
                Text(
                    text = "${String.format(Locale.US, "%,d", country.totalPlayers)} Chiến Thần Tham Gia",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.5.sp,
                    color = ComicInkBlack.copy(alpha = 0.6f)
                )
            }

            // Total Country Meters
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${String.format(Locale.US, "%,.0f", country.totalMeters / 1000.0)} km",
                    fontFamily = ComicFontFamily,
                    fontSize = 15.sp,
                    color = if (isTop1) Color(0xFFFF5722) else ComicInkBlack
                )
                Text(
                    text = "tổng cộng",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = ComicInkBlack.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun LeaderboardTabPill(
    title: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(if (isSelected) 4.dp else 1.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) selectedColor else Color.White)
            .border(
                width = 2.5.dp,
                color = ComicInkBlack,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontFamily = ComicFontFamily,
            fontSize = 12.sp,
            color = ComicInkBlack
        )
    }
}

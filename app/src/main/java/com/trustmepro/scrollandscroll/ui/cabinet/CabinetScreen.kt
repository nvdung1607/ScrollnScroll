package com.trustmepro.scrollandscroll.ui.cabinet

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.trustmepro.scrollandscroll.data.model.BadgeType
import com.trustmepro.scrollandscroll.data.model.SkinType
import com.trustmepro.scrollandscroll.ui.cabinet.components.BadgeCard
import com.trustmepro.scrollandscroll.ui.cabinet.components.SkinCard
import com.trustmepro.scrollandscroll.ui.cabinet.components.SkinItemState
import com.trustmepro.scrollandscroll.ui.components.ComicCircleButton
import com.trustmepro.scrollandscroll.ui.components.ComicCyan
import com.trustmepro.scrollandscroll.ui.components.ComicGold
import com.trustmepro.scrollandscroll.ui.components.ComicInkBlack
import com.trustmepro.scrollandscroll.ui.components.ComicYellow
import com.trustmepro.scrollandscroll.ui.theme.ComicFontFamily
import java.util.Locale

/**
 * Màn hình Tủ Đồ Skin & Danh Hiệu Bằng Khen chuẩn phong cách Comic Pop-Art
 */
@Composable
fun CabinetScreen(
    onBack: () -> Unit,
    viewModel: CabinetViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF9E6)) // Nền vàng kem ấm áp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // ── 1. Top Bar Header ─────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nút quay lại
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

                Spacer(Modifier.size(12.dp))

                // Tiêu đề màn hình
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TỦ ĐỒ & DANH HIỆU",
                        fontFamily = ComicFontFamily,
                        fontSize = 22.sp,
                        color = ComicInkBlack
                    )
                    Text(
                        text = "Đã cuộn: ${String.format(Locale.US, "%,.1f", uiState.totalMeters)} mét",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ComicInkBlack.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // ── 2. Tabs Switcher (SKIN GIẤY vs BẰNG KHEN) ───────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val isSkinsTab = uiState.selectedTab == CabinetTab.SKINS
                val unlockedSkinsCount = uiState.unlockedSkinIds.size
                CabinetTabPill(
                    title = "🧻 SKIN GIẤY ($unlockedSkinsCount/9)",
                    isSelected = isSkinsTab,
                    selectedColor = ComicYellow,
                    onClick = { viewModel.selectTab(CabinetTab.SKINS) },
                    modifier = Modifier.weight(1f)
                )

                val unlockedBadgesCount = uiState.unlockedBadgeIds.size
                CabinetTabPill(
                    title = "📜 BẰNG KHEN ($unlockedBadgesCount/8)",
                    isSelected = !isSkinsTab,
                    selectedColor = ComicGold,
                    onClick = { viewModel.selectTab(CabinetTab.BADGES) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            // ── 3. Content Body ──────────────────────────────────────────────
            when (uiState.selectedTab) {
                CabinetTab.SKINS -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(SkinType.entries) { skin ->
                            val isEquipped = skin == uiState.selectedSkin
                            val isUnlocked = uiState.unlockedSkinIds.contains(skin.id) || skin.requiredMeters == 0.0

                            val state = when {
                                isEquipped -> SkinItemState.EQUIPPED
                                isUnlocked -> SkinItemState.UNLOCKED
                                else -> SkinItemState.LOCKED
                            }

                            SkinCard(
                                skin = skin,
                                state = state,
                                currentMeters = uiState.totalMeters,
                                onSelect = { viewModel.equipSkin(skin) }
                            )
                        }
                    }
                }

                CabinetTab.BADGES -> {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(BadgeType.entries) { badge ->
                            val isUnlocked = uiState.unlockedBadgeIds.contains(badge.id) || uiState.totalMeters >= badge.requiredMeters

                            BadgeCard(
                                badge = badge,
                                isUnlocked = isUnlocked,
                                onViewCertificate = { viewModel.showCertificate(badge) }
                            )
                        }
                    }
                }
            }
        }

        // ── 4. Modal Bằng Khen Danh Dự khi bấm Xem Bằng Khen ────────────
        uiState.selectedBadgeForCertificate?.let { badge ->
            com.trustmepro.scrollandscroll.ui.components.CertificateDialog(
                badge = badge,
                nickname = uiState.nickname,
                totalMeters = uiState.totalMeters,
                onDismiss = { viewModel.dismissCertificate() }
            )
        }
    }
}

@Composable
private fun CabinetTabPill(
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
            fontSize = 13.sp,
            color = ComicInkBlack
        )
    }
}

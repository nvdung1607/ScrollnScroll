package com.trustmepro.scrollandscroll.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.trustmepro.scrollandscroll.data.model.BadgeType
import com.trustmepro.scrollandscroll.ui.components.Comic3DWordmark
import com.trustmepro.scrollandscroll.ui.components.ComicCard
import com.trustmepro.scrollandscroll.ui.components.ComicCertificateDocIcon
import com.trustmepro.scrollandscroll.ui.components.ComicCircleButton
import com.trustmepro.scrollandscroll.ui.components.ComicCyan
import com.trustmepro.scrollandscroll.ui.components.ComicGearIcon
import com.trustmepro.scrollandscroll.ui.components.ComicGreen
import com.trustmepro.scrollandscroll.ui.components.ComicHangerIcon
import com.trustmepro.scrollandscroll.ui.components.ComicInkBlack
import com.trustmepro.scrollandscroll.ui.components.ComicOdometerDisplay
import com.trustmepro.scrollandscroll.ui.components.ComicOrange
import com.trustmepro.scrollandscroll.ui.components.ComicScrollCertificateIcon
import com.trustmepro.scrollandscroll.ui.components.ComicSpeakerIcon
import com.trustmepro.scrollandscroll.ui.components.ComicTrophyIcon
import com.trustmepro.scrollandscroll.ui.components.ComicYellow
import com.trustmepro.scrollandscroll.ui.components.M3PrimaryButton
import com.trustmepro.scrollandscroll.ui.game.components.OverdriveEffect
import com.trustmepro.scrollandscroll.ui.game.components.SpsGauge
import com.trustmepro.scrollandscroll.ui.theme.ComicFontFamily
import java.util.Locale


// ─────────────────────────────────────────────────────────────────────────────
// GameScreen — Màn hình chơi chính chuẩn 100% phong cách Comic Pop-Art
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel(),
    onNavigateToLeaderboard: () -> Unit = {},
    onNavigateToCabinet: (tab: String) -> Unit = {},
    onOpenSettings: () -> Unit = {}
) {
    val ui by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {

        // ── 1. Canvas tương tác Cuộn giấy 3D & Dải giấy uốn lượn ───────────
        ToiletPaperCanvas(
            skin = ui.selectedSkin,
            isOverdrive = ui.isOverdrive,
            onScroll = { px, vel -> viewModel.onScroll(px, vel) },
            modifier = Modifier.fillMaxSize()
        )

        // ── 2. Tiêu đề "SCROLL & SCROLL" 3D Pop-Art ở đỉnh màn hình ────────
        Comic3DWordmark(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 4.dp)
        )

        // ── 3. Nút Setting (Góc trên bên trái) ──────────────────────────────
        ComicCircleButton(
            label = "Setting",
            containerColor = ComicCyan,
            onClick = onOpenSettings,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 14.dp, top = 8.dp)
        ) {
            ComicGearIcon(modifier = Modifier.fillMaxSize())
        }

        // ── 4. Cụm nút Bảng Xếp Hạng & Tủ Đồ Skin (Góc trên bên phải) ─────
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(end = 14.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ComicCircleButton(
                label = "Bảng Xếp Hạng",
                containerColor = ComicYellow,
                onClick = onNavigateToLeaderboard
            ) {
                ComicTrophyIcon(modifier = Modifier.fillMaxSize())
            }
            ComicCircleButton(
                label = "Tủ Đồ Skin",
                containerColor = ComicGreen,
                onClick = { onNavigateToCabinet("skins") }
            ) {
                ComicHangerIcon(modifier = Modifier.fillMaxSize())
            }
        }

        // ── 5. Chỉ dẫn vuốt lên/xuống ở cạnh phải màn hình (Mũi tên 2 đầu tinh tế ~2.5cm) ──────
        RightEdgeSwipeGuide(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 6.dp)
        )

        // ── 6. Hàng điều khiển đáy màn hình: [ASMR] [HUD CARD] [BẰNG KHEN] ──
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Nút ASMR (Góc dưới bên trái)
            ComicCircleButton(
                label = if (ui.isSoundEnabled) "ASMR: Bật" else "ASMR: Tắt",
                containerColor = ComicCyan,
                onClick = { viewModel.toggleSound() },
                size = 56.dp
            ) {
                ComicSpeakerIcon(modifier = Modifier.fillMaxSize())
            }

            // Thẻ HUD Odometer đo khoảng cách trung tâm (Giữa)
            ComicCard(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                ComicOdometerDisplay(meters = ui.totalMeters)
            }

            // Nút Bằng Khen Của Tôi (Góc dưới bên phải - Mở ngay tab Bằng Khen)
            ComicCircleButton(
                label = "Bằng Khen\nCủa Tôi",
                containerColor = ComicOrange,
                onClick = { onNavigateToCabinet("badges") },
                size = 56.dp
            ) {
                ComicScrollCertificateIcon(modifier = Modifier.fillMaxSize())
            }
        }

        // ── 7. Hiệu ứng viền lửa rực rỡ khi vào chế độ Overdrive ───────────
        OverdriveEffect(isOverdrive = ui.isOverdrive)

        // ── 8. Modal Bằng Khen Danh Dự Vô Tri khi chạm mốc mới ───────────
        ui.newlyUnlockedBadge?.let { badge ->
            com.trustmepro.scrollandscroll.ui.components.CertificateDialog(
                badge = badge,
                nickname = ui.nickname,
                totalMeters = ui.totalMeters,
                onDismiss = { viewModel.dismissBadgeDialog() }
            )
        }

        // ── 9. Modal Chúc Mừng Mở Khóa Skin Mới (Hiện kế tiếp sau Bằng Khen) ──
        if (ui.newlyUnlockedBadge == null) {
            ui.newlyUnlockedSkin?.let { skin ->
                com.trustmepro.scrollandscroll.ui.components.NewSkinUnlockDialog(
                    skin = skin,
                    onEquipNow = { viewModel.equipNewlyUnlockedSkin(skin) },
                    onDismiss = { viewModel.dismissSkinDialog() }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Mũi tên 2 đầu chỉ dẫn vuốt lên/xuống ở cạnh phải (~2.5cm, tinh tế, không chói)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun RightEdgeSwipeGuide(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "swipeArrowAnim")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "arrowFloat"
    )

    Box(
        modifier = modifier
            .offset(y = offsetY.dp)
            .shadow(3.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.85f))
            .border(2.dp, ComicInkBlack, RoundedCornerShape(12.dp))
            .padding(horizontal = 4.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Mũi tên chỉ lên
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = null,
                tint = ComicInkBlack,
                modifier = Modifier
                    .size(20.dp)
                    .offset(y = 2.dp)
            )

            // Thân trục dài (khoảng 2.5 - 3cm thực tế trên màn hình)
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(48.dp)
                    .background(ComicInkBlack)
            )

            // Mũi tên chỉ xuống
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = ComicInkBlack,
                modifier = Modifier
                    .size(20.dp)
                    .offset(y = (-2).dp)
            )
        }
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// Popup chúc mừng mở khóa Bằng Khen mới
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun BadgeUnlockDialog(
    badge: BadgeType,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            M3PrimaryButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🔥 TIẾP TỤC CUỘN!")
            }
        },
        icon = {
            Text(text = badge.badgeEmoji, fontSize = 50.sp)
        },
        title = {
            Text(
                text = "MỞ KHÓA DANH HIỆU MỚI!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "[ ${badge.title} ]",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"${badge.description}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

package com.trustmepro.scrollandscroll.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.StarRate
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
import com.trustmepro.scrollandscroll.ui.components.ComicCircleButton
import com.trustmepro.scrollandscroll.ui.components.ComicCyan
import com.trustmepro.scrollandscroll.ui.components.ComicGreen
import com.trustmepro.scrollandscroll.ui.components.ComicInkBlack
import com.trustmepro.scrollandscroll.ui.components.ComicOrange
import com.trustmepro.scrollandscroll.ui.components.ComicYellow
import com.trustmepro.scrollandscroll.ui.components.M3PrimaryButton
import com.trustmepro.scrollandscroll.ui.game.components.OverdriveEffect
import com.trustmepro.scrollandscroll.ui.game.components.SpsGauge
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// GameScreen — Màn hình chơi chính chuẩn 100% phong cách Comic Pop-Art
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel(),
    onNavigateToLeaderboard: () -> Unit = {},
    onNavigateToCabinet: () -> Unit = {},
    onOpenSettings: () -> Unit = {}
) {
    val ui by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {

        // ── 1. Canvas tương tác Cuộn giấy 3D & Dải giấy uốn lượn ───────────
        ToiletPaperCanvas(
            skin = ui.selectedSkin,
            isOverdrive = ui.isOverdrive,
            onScroll = { px -> viewModel.onScroll(px) },
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
            icon = Icons.Default.Settings,
            label = "Setting",
            containerColor = ComicCyan,
            onClick = onOpenSettings,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 14.dp, top = 8.dp)
        )

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
                icon = Icons.Default.EmojiEvents,
                label = "Bảng Xếp Hạng",
                containerColor = ComicYellow,
                onClick = onNavigateToLeaderboard
            )
            ComicCircleButton(
                icon = Icons.Default.Checkroom,
                label = "Tủ Đồ Skin",
                containerColor = ComicGreen,
                onClick = onNavigateToCabinet
            )
        }

        // ── 5. Hướng dẫn cử chỉ vuốt cho tân thủ (Tự ẩn sau 3 lần vuốt) ────
        TutorialSwipeOverlay(
            visible = ui.totalSwipes < 3,
            modifier = Modifier.fillMaxSize()
        )

        // ── 6. Hàng điều khiển đáy màn hình: [ASMR] [HUD CARD] [BẰNG KHEN] ──
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Nút ASMR (Góc dưới bên trái)
            ComicCircleButton(
                icon = if (ui.isSoundEnabled)
                    Icons.AutoMirrored.Filled.VolumeUp
                else
                    Icons.AutoMirrored.Filled.VolumeOff,
                label = if (ui.isSoundEnabled) "ASMR: Bật" else "ASMR: Tắt",
                containerColor = ComicCyan,
                onClick = { viewModel.toggleSound() }
            )

            // Thẻ HUD Odometer đo khoảng cách trung tâm (Giữa)
            ComicCard(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 6.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "TỔNG KHOẢNG CÁCH",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = ComicInkBlack,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(Modifier.height(4.dp))

                    // Hộp lật số cơ học màu đen retro: [ 12,458.6 MET ]
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ComicInkBlack)
                            .border(2.dp, ComicInkBlack, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${String.format(Locale.US, "%,.1f", ui.totalMeters)} MET",
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White,
                            fontSize = 20.sp,
                            letterSpacing = 1.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Quy đổi km
                    Text(
                        text = String.format(Locale.US, "(~ %.2f km)", ui.totalMeters / 1000.0),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.5.sp,
                        color = ComicInkBlack.copy(alpha = 0.75f),
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    // Thanh đo tốc độ SPS
                    Spacer(Modifier.height(4.dp))
                    SpsGauge(sps = ui.currentSps, isOverdrive = ui.isOverdrive)

                    // Thanh tiến độ mở khóa Skin tiếp theo
                    ui.nextSkin?.let { next ->
                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Mở khóa ${next.patternEmoji} ${next.displayName}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ComicInkBlack.copy(alpha = 0.85f)
                            )
                            Text(
                                text = String.format(Locale.US, "%.0f%%", ui.nextSkinProgress * 100f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(Modifier.height(2.dp))
                        LinearProgressIndicator(
                            progress = { ui.nextSkinProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = Color(0xFFEDE7D9)
                        )
                    }
                }
            }

            // Cụm 2 nút Bằng Khen (Góc dưới bên phải)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ComicCircleButton(
                    icon = Icons.Default.MilitaryTech,
                    label = "Bằng Khen\nCủa Tôi",
                    containerColor = ComicOrange,
                    onClick = onNavigateToCabinet,
                    size = 50.dp
                )
                ComicCircleButton(
                    icon = Icons.Default.StarRate,
                    label = "Thành Tích",
                    containerColor = ComicYellow,
                    onClick = onNavigateToCabinet,
                    size = 50.dp
                )
            }
        }

        // ── 7. Hiệu ứng viền lửa rực rỡ khi vào chế độ Overdrive ───────────
        OverdriveEffect(isOverdrive = ui.isOverdrive)

        // ── 8. Dialog chúc mừng khi mở khóa Bằng Khen danh hiệu mới ───────
        ui.newlyUnlockedBadge?.let { badge ->
            BadgeUnlockDialog(
                badge = badge,
                onDismiss = { viewModel.dismissBadgeDialog() }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Chỉ dẫn vuốt xuống cho người mới chơi
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TutorialSwipeOverlay(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "swipeAnim")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 16f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Bàn tay chỉ ngón 👆 chạm vào giấy trên
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = (-40).dp, y = (-20).dp + offsetY.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "👆", fontSize = 36.sp)
            }

            // Hai mũi tên chỉ xuống kèm chữ SWIPE DOWN
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 80.dp + offsetY.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ComicInkBlack.copy(alpha = 0.88f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFFFFD54F),
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "SWIPE  DOWN",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFFFFD54F),
                    modifier = Modifier.size(18.dp)
                )
            }
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

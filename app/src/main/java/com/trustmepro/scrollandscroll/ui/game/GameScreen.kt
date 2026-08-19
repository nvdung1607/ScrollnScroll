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
import androidx.compose.foundation.layout.widthIn
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
import com.trustmepro.scrollandscroll.ui.components.ComicCard
import com.trustmepro.scrollandscroll.ui.components.ComicCircleButton
import com.trustmepro.scrollandscroll.ui.components.ComicGold
import com.trustmepro.scrollandscroll.ui.components.ComicGray
import com.trustmepro.scrollandscroll.ui.components.ComicGreen
import com.trustmepro.scrollandscroll.ui.components.ComicInkBlack
import com.trustmepro.scrollandscroll.ui.components.ComicOrange
import com.trustmepro.scrollandscroll.ui.components.ComicYellow
import com.trustmepro.scrollandscroll.ui.components.M3PrimaryButton
import com.trustmepro.scrollandscroll.ui.game.components.OverdriveEffect
import com.trustmepro.scrollandscroll.ui.game.components.SpsGauge
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// GameScreen — Màn hình chơi chính
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

        // ── Lớp 1: Canvas cuộn giấy tương tác ──────────────────────────────
        ToiletPaperCanvas(
            skin        = ui.selectedSkin,
            isOverdrive = ui.isOverdrive,
            onScroll    = { px -> viewModel.onScroll(px) },
            modifier    = Modifier.fillMaxSize()
        )

        // ── Lớp 2: Tiêu đề "SCROLL & SCROLL" (góc trên, giữa) ──────────────
        ComicGameTitle(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 6.dp)
        )

        // ── Lớp 3: Nút Setting (góc trên trái) ──────────────────────────────
        ComicCircleButton(
            icon           = Icons.Default.Settings,
            label          = "Setting",
            containerColor = ComicGray,
            onClick        = onOpenSettings,
            modifier       = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 14.dp, top = 8.dp)
        )

        // ── Lớp 4: Nút Bảng XH + Tủ Đồ Skin (góc trên phải, cột) ──────────
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(end = 14.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ComicCircleButton(
                icon           = Icons.Default.EmojiEvents,
                label          = "Bảng Xếp\nHạng",
                containerColor = ComicGold,
                onClick        = onNavigateToLeaderboard
            )
            ComicCircleButton(
                icon           = Icons.Default.Checkroom,
                label          = "Tủ Đồ\nSkin",
                containerColor = ComicGreen,
                onClick        = onNavigateToCabinet
            )
        }

        // ── Lớp 5: Gợi ý vuốt cho người mới ────────────────────────────────
        SwipeHelperPrompt(
            visible  = ui.totalSwipes < 4,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 110.dp)
        )

        // ── Lớp 6: Phần dưới — Row gồm [ASMR] [HUD Card] [Bằng Khen] ───────
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(start = 12.dp, end = 12.dp, bottom = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // ── Nút ASMR (trái) ─────────────────────────────────────────────
            ComicCircleButton(
                icon           = if (ui.isSoundEnabled)
                    Icons.AutoMirrored.Filled.VolumeUp
                else
                    Icons.AutoMirrored.Filled.VolumeOff,
                label          = if (ui.isSoundEnabled) "ASMR: Bật" else "ASMR: Tắt",
                containerColor = ComicGray,
                onClick        = { viewModel.toggleSound() }
            )

            // ── HUD Card Odometer (giữa) ─────────────────────────────────────
            ComicCard(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Column(
                    modifier             = Modifier.fillMaxWidth(),
                    horizontalAlignment  = Alignment.CenterHorizontally
                ) {
                    // Header
                    Text(
                        text       = "TỔNG KHOẢNG CÁCH",
                        fontWeight = FontWeight.Black,
                        fontSize   = 12.sp,
                        color      = ComicInkBlack,
                        letterSpacing = 0.4.sp
                    )

                    Spacer(Modifier.height(4.dp))

                    // Hộp số đen — số mét theo dạng "12,458.6 MET"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(ComicInkBlack)
                            .border(2.dp, ComicInkBlack, RoundedCornerShape(10.dp))
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text         = "${String.format(Locale.US, "%,.1f", ui.totalMeters)} MET",
                            fontWeight   = FontWeight.Black,
                            fontFamily   = FontFamily.Monospace,
                            color        = Color.White,
                            fontSize     = 20.sp,
                            letterSpacing = 0.8.sp,
                            textAlign    = TextAlign.Center
                        )
                    }

                    // Quy đổi km
                    Text(
                        text       = String.format(Locale.US, "(~ %.2f km)", ui.totalMeters / 1000.0),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize   = 11.sp,
                        color      = ComicInkBlack.copy(alpha = 0.65f),
                        modifier   = Modifier.padding(top = 2.dp)
                    )

                    // SPS Gauge
                    Spacer(Modifier.height(6.dp))
                    SpsGauge(sps = ui.currentSps, isOverdrive = ui.isOverdrive)

                    // Tiến độ mở skin tiếp theo
                    ui.nextSkin?.let { next ->
                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text       = "▶ ${next.patternEmoji} ${next.displayName}",
                                fontSize   = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color      = ComicInkBlack.copy(0.8f)
                            )
                            Text(
                                text       = "%.0f%%".format(ui.nextSkinProgress * 100f),
                                fontSize   = 10.sp,
                                fontWeight = FontWeight.Black,
                                color      = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(Modifier.height(3.dp))
                        LinearProgressIndicator(
                            progress     = { ui.nextSkinProgress },
                            modifier     = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color        = MaterialTheme.colorScheme.primary,
                            trackColor   = Color(0xFFEDE7D9)
                        )
                    }
                }
            }

            // ── Hai nút Bằng Khen (phải, cột) ───────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ComicCircleButton(
                    icon           = Icons.Default.MilitaryTech,
                    label          = "Bằng Khen\nCủa Tôi",
                    containerColor = ComicYellow,
                    onClick        = onNavigateToCabinet
                )
                ComicCircleButton(
                    icon           = Icons.Default.StarRate,
                    label          = "Thành\nTích",
                    containerColor = ComicOrange,
                    onClick        = onNavigateToCabinet
                )
            }
        }

        // ── Lớp 7: Hiệu ứng Overdrive ───────────────────────────────────────
        OverdriveEffect(isOverdrive = ui.isOverdrive)

        // ── Lớp 8: Dialog thông báo mở khóa Bằng Khen ──────────────────────
        ui.newlyUnlockedBadge?.let { badge ->
            BadgeUnlockDialog(
                badge     = badge,
                onDismiss = { viewModel.dismissBadgeDialog() }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ComicGameTitle — "SCROLL & SCROLL" kiểu 3D cartoon, xếp thành 3 dòng
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ComicGameTitle(modifier: Modifier = Modifier) {
    Column(
        modifier            = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // SCROLL (dòng 1)
        ComicLetterBlock(text = "SCROLL", fontSize = 34.sp)

        // "&" (nhỏ hơn, ở giữa)
        Box(contentAlignment = Alignment.Center) {
            // Shadow layer
            Text(
                text       = "&",
                fontSize   = 20.sp,
                fontWeight = FontWeight.Black,
                color      = ComicInkBlack,
                modifier   = Modifier.offset(x = 1.5.dp, y = 2.dp)
            )
            // Main orange fill
            Text(
                text       = "&",
                fontSize   = 20.sp,
                fontWeight = FontWeight.Black,
                color      = Color(0xFFFF7D00)
            )
        }

        // SCROLL (dòng 2)
        ComicLetterBlock(text = "SCROLL", fontSize = 34.sp)

        Spacer(Modifier.height(4.dp))

        // Pill "CUỘN GIẤY VỆ SINH VÔ TẬN"
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(ComicInkBlack)
                .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
            Text(
                text          = "CUỘN GIẤY VỆ SINH VÔ TẬN",
                fontSize      = 11.sp,
                fontWeight    = FontWeight.Black,
                letterSpacing = 0.7.sp,
                color         = Color(0xFFFFD54F)
            )
        }
    }
}

/**
 * Một từ kiểu chữ 3D cartoon:
 * — Lớp dưới: nét đen offset (tạo bóng/viền)
 * — Lớp trên: màu cam-đỏ
 */
@Composable
private fun ComicLetterBlock(text: String, fontSize: androidx.compose.ui.unit.TextUnit) {
    Box(contentAlignment = Alignment.Center) {
        // Nét đen (shadow layer)
        Text(
            text       = text,
            fontSize   = fontSize,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            color      = ComicInkBlack,
            modifier   = Modifier.offset(x = 2.dp, y = 3.dp)
        )
        // Nét viền bên phải
        Text(
            text       = text,
            fontSize   = fontSize,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            color      = ComicInkBlack,
            modifier   = Modifier.offset(x = (-1).dp, y = (-1).dp)
        )
        // Màu chính cam-đỏ
        Text(
            text       = text,
            fontSize   = fontSize,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            color      = Color(0xFFFF5200)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SwipeHelperPrompt — Gợi ý vuốt cho người mới chơi
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SwipeHelperPrompt(visible: Boolean, modifier: Modifier = Modifier) {
    val anim = rememberInfiniteTransition(label = "swipe")
    val dy by anim.animateFloat(
        initialValue   = -10f,
        targetValue    = 16f,
        animationSpec  = infiniteRepeatable(tween(550), RepeatMode.Reverse),
        label          = "swipeY"
    )

    AnimatedVisibility(
        visible = visible,
        enter   = fadeIn(),
        exit    = fadeOut(),
        modifier = modifier
    ) {
        Column(
            modifier            = Modifier.offset(y = dy.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("👆", fontSize = 38.sp)
            Spacer(Modifier.height(4.dp))
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier              = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ComicInkBlack.copy(alpha = 0.88f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFFFFD54F),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text       = "SWIPE DOWN",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 13.sp,
                    color      = Color.White,
                    modifier   = Modifier.padding(horizontal = 4.dp)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFFFFD54F),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// BadgeUnlockDialog — Popup chúc mừng mở khóa Bằng Khen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun BadgeUnlockDialog(badge: BadgeType, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            M3PrimaryButton(
                onClick  = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) { Text("🔥 TIẾP TỤC CUỘN!") }
        },
        icon = { Text(text = badge.badgeEmoji, fontSize = 52.sp) },
        title = {
            Text(
                text       = "MỞ KHÓA DANH HIỆU MỚI!",
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                textAlign  = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier            = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text       = "[ ${badge.title} ]",
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.primary,
                    textAlign  = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text      = "\"${badge.description}\"",
                    style     = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color     = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        shape          = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

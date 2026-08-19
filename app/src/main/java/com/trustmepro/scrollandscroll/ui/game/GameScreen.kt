package com.trustmepro.scrollandscroll.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.trustmepro.scrollandscroll.data.model.BadgeType
import com.trustmepro.scrollandscroll.ui.components.M3ElevatedCard
import com.trustmepro.scrollandscroll.ui.components.M3IconButton
import com.trustmepro.scrollandscroll.ui.components.M3PrimaryButton
import com.trustmepro.scrollandscroll.ui.components.OdometerText
import com.trustmepro.scrollandscroll.ui.game.components.OverdriveEffect
import com.trustmepro.scrollandscroll.ui.game.components.SpsGauge
import com.trustmepro.scrollandscroll.ui.theme.Gold24K
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel(),
    onNavigateToLeaderboard: () -> Unit = {},
    onNavigateToCabinet: () -> Unit = {},
    onOpenSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🧻 ${uiState.selectedSkin.displayName}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                },
                actions = {
                    M3IconButton(
                        onClick = onNavigateToLeaderboard,
                        icon = Icons.Default.EmojiEvents,
                        contentDescription = "Leaderboard",
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        size = 42.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    M3IconButton(
                        onClick = onNavigateToCabinet,
                        icon = Icons.Default.Palette,
                        contentDescription = "Cabinet",
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        size = 42.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    M3IconButton(
                        onClick = { viewModel.toggleSound() },
                        icon = if (uiState.isSoundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                        contentDescription = "Toggle Sound",
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        size = 42.dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    M3IconButton(
                        onClick = onOpenSettings,
                        icon = Icons.Default.Settings,
                        contentDescription = "Settings",
                        size = 42.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // 1. Odometer Hero Display
                OdometerText(
                    meters = uiState.totalMeters,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                // 2. Next Skin Progress Bar
                uiState.nextSkin?.let { next ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Mở khóa ${next.patternEmoji} ${next.displayName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = String.format(Locale.US, "%.0f%%", uiState.nextSkinProgress * 100f),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { uiState.nextSkinProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 3. SPS Realtime Gauge
                SpsGauge(
                    sps = uiState.currentSps,
                    isOverdrive = uiState.isOverdrive
                )

                // 4. Interactive Toilet Paper Canvas (Core Area)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    ToiletPaperCanvas(
                        skin = uiState.selectedSkin,
                        isOverdrive = uiState.isOverdrive,
                        onScroll = { pixels -> viewModel.onScroll(pixels) }
                    )
                }
            }

            // 5. Overdrive Visual FX Border
            OverdriveEffect(isOverdrive = uiState.isOverdrive)

            // 6. Celebration Pop-up Dialog for Newly Unlocked Badge
            uiState.newlyUnlockedBadge?.let { badge ->
                BadgeUnlockDialog(
                    badge = badge,
                    onDismiss = { viewModel.dismissBadgeDialog() }
                )
            }
        }
    }
}

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
            Text(
                text = badge.badgeEmoji,
                fontSize = 48.sp
            )
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

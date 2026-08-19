package com.trustmepro.scrollandscroll.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustmepro.scrollandscroll.ui.components.ComicCard
import com.trustmepro.scrollandscroll.ui.components.ComicInkBlack
import com.trustmepro.scrollandscroll.ui.components.ComicOrange
import com.trustmepro.scrollandscroll.ui.components.ComicYellow
import com.trustmepro.scrollandscroll.ui.components.M3PrimaryButton
import com.trustmepro.scrollandscroll.ui.components.M3TonalButton
import com.trustmepro.scrollandscroll.ui.theme.ComicFontFamily

/**
 * Modal Cài Đặt (Âm thanh ASMR, Rung Haptic, Đổi Tên, Phiên bản)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    isSoundEnabled: Boolean,
    isHapticEnabled: Boolean,
    nickname: String,
    onToggleSound: (Boolean) -> Unit,
    onToggleHaptic: (Boolean) -> Unit,
    onChangeNickname: () -> Unit,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFFFFDF0))
                .border(width = 4.dp, color = ComicInkBlack, shape = RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header + Nút đóng
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "⚙️ CÀI ĐẶT GAME",
                        fontFamily = ComicFontFamily,
                        fontSize = 22.sp,
                        color = ComicInkBlack
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ComicInkBlack
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Item 1: Âm thanh ASMR
                SettingToggleRow(
                    icon = Icons.Default.VolumeUp,
                    title = "Âm Thanh ASMR Giấy",
                    subtitle = "Tiếng sột soạt ma sát giấy cuộn chân thực",
                    checked = isSoundEnabled,
                    onCheckedChange = onToggleSound
                )

                Spacer(Modifier.height(10.dp))

                // Item 2: Rung Xúc Giác Haptic
                SettingToggleRow(
                    icon = Icons.Default.Vibration,
                    title = "Rung Xúc Giác Haptic",
                    subtitle = "Cảm giác nảy nhẹ theo từng vòng xoay cuộn giấy",
                    checked = isHapticEnabled,
                    onCheckedChange = onToggleHaptic
                )

                Spacer(Modifier.height(12.dp))

                // Item 3: Biệt danh người chơi
                ComicCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.White
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Biệt Danh Hiện Tại",
                                style = MaterialTheme.typography.labelSmall,
                                color = ComicInkBlack.copy(alpha = 0.6f)
                            )
                            Text(
                                text = if (nickname.isBlank()) "Chiến Thần Giấu Tên" else nickname,
                                fontFamily = ComicFontFamily,
                                fontSize = 16.sp,
                                color = ComicInkBlack
                            )
                        }

                        M3TonalButton(onClick = onChangeNickname) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.size(4.dp))
                            Text("Đổi Tên", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Thông tin phiên bản & tác quyền
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Scroll & Scroll v1.0.0",
                        fontFamily = ComicFontFamily,
                        fontSize = 12.sp,
                        color = ComicInkBlack.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "Sản phẩm 100% Vô Tri được chế tác với niềm đam mê",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = ComicInkBlack.copy(alpha = 0.4f)
                    )
                }

                Spacer(Modifier.height(14.dp))

                // Nút Đóng
                M3PrimaryButton(
                    onClick = onDismiss,
                    containerColor = ComicYellow,
                    contentColor = ComicInkBlack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("LƯU & ĐÓNG", fontFamily = ComicFontFamily, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ComicCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ComicInkBlack,
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.size(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontFamily = ComicFontFamily,
                    fontSize = 14.sp,
                    color = ComicInkBlack
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.5.sp,
                    color = ComicInkBlack.copy(alpha = 0.6f)
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = ComicOrange,
                    uncheckedThumbColor = ComicInkBlack.copy(alpha = 0.6f),
                    uncheckedTrackColor = Color(0xFFE0E0E0)
                )
            )
        }
    }
}

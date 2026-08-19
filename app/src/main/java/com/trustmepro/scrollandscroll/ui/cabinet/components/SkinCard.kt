package com.trustmepro.scrollandscroll.ui.cabinet.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.trustmepro.scrollandscroll.data.model.SkinType
import com.trustmepro.scrollandscroll.ui.components.ComicCard
import com.trustmepro.scrollandscroll.ui.components.ComicGreen
import com.trustmepro.scrollandscroll.ui.components.ComicInkBlack
import com.trustmepro.scrollandscroll.ui.components.ComicYellow
import com.trustmepro.scrollandscroll.ui.components.M3PrimaryButton
import com.trustmepro.scrollandscroll.ui.theme.ComicFontFamily
import java.util.Locale

enum class SkinItemState {
    EQUIPPED,
    UNLOCKED,
    LOCKED
}

/**
 * Thẻ hiển thị Skin giấy vệ sinh phong cách Comic Pop-Art với 3 trạng thái
 */
@Composable
fun SkinCard(
    skin: SkinType,
    state: SkinItemState,
    currentMeters: Double,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLocked = state == SkinItemState.LOCKED
    val isEquipped = state == SkinItemState.EQUIPPED

    val cardBg = if (isLocked) Color(0xFFF3F3F3) else Color.White

    ComicCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = cardBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Preview Icon / Emoji hình tròn nổi bật
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = if (isLocked) {
                                listOf(Color(0xFFE0E0E0), Color(0xFFBDBDBD))
                            } else {
                                listOf(skin.primaryColor.copy(alpha = 0.9f), skin.primaryColor, skin.accentColor.copy(alpha = 0.6f))
                            }
                        )
                    )
                    .border(
                        width = 2.5.dp,
                        color = if (isLocked) ComicInkBlack.copy(alpha = 0.3f) else ComicInkBlack,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = ComicInkBlack.copy(alpha = 0.5f),
                        modifier = Modifier.size(32.dp)
                    )
                } else {
                    Text(
                        text = skin.patternEmoji,
                        fontSize = 38.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // 2. Tên Skin
            Text(
                text = skin.displayName,
                fontFamily = ComicFontFamily,
                fontSize = 15.sp,
                color = if (isLocked) ComicInkBlack.copy(alpha = 0.5f) else ComicInkBlack,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(3.dp))

            // 3. Mô tả ngắn
            Text(
                text = skin.description,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = ComicInkBlack.copy(alpha = 0.65f),
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(Modifier.height(8.dp))

            // 4. Trạng thái & Nút hành động
            when (state) {
                SkinItemState.EQUIPPED -> {
                    // Huy hiệu "ĐANG DÙNG"
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ComicGreen)
                            .border(2.dp, ComicInkBlack, RoundedCornerShape(12.dp))
                            .padding(vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = ComicInkBlack,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = " ĐANG DÙNG",
                            fontFamily = ComicFontFamily,
                            fontSize = 12.sp,
                            color = ComicInkBlack
                        )
                    }
                }

                SkinItemState.UNLOCKED -> {
                    // Nút "SỬ DỤNG"
                    M3PrimaryButton(
                        onClick = onSelect,
                        containerColor = ComicYellow,
                        contentColor = ComicInkBlack,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "SỬ DỤNG",
                            fontFamily = ComicFontFamily,
                            fontSize = 13.sp
                        )
                    }
                }

                SkinItemState.LOCKED -> {
                    // Thanh tiến độ mở khóa %
                    val progress = (currentMeters / skin.requiredMeters).coerceIn(0.0, 1.0).toFloat()
                    val percent = (progress * 100f).toInt()

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Mở ở: ${String.format(Locale.US, "%,.0fm", skin.requiredMeters)}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = ComicInkBlack.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "$percent%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(Modifier.height(3.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = Color(0xFFE0E0E0)
                        )
                    }
                }
            }
        }
    }
}

package com.trustmepro.scrollandscroll.ui.cabinet.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material3.Icon
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
import com.trustmepro.scrollandscroll.data.model.BadgeType
import com.trustmepro.scrollandscroll.ui.components.ComicCard
import com.trustmepro.scrollandscroll.ui.components.ComicGold
import com.trustmepro.scrollandscroll.ui.components.ComicInkBlack
import com.trustmepro.scrollandscroll.ui.components.ComicOrange
import com.trustmepro.scrollandscroll.ui.components.ComicYellow
import com.trustmepro.scrollandscroll.ui.theme.ComicFontFamily
import java.util.Locale

/**
 * Thẻ hiển thị Danh Hiệu Bằng Khen phong cách trào phúng Comic Pop-Art
 */
@Composable
fun BadgeCard(
    badge: BadgeType,
    isUnlocked: Boolean,
    onViewCertificate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBg = if (isUnlocked) Color.White else Color(0xFFF3F3F3)

    ComicCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = cardBg
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Badge Icon / Emoji
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(3.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        brush = if (isUnlocked) {
                            Brush.radialGradient(
                                colors = listOf(Color(0xFFFFF9C4), ComicYellow, ComicGold)
                            )
                        } else {
                            Brush.radialGradient(
                                colors = listOf(Color(0xFFE0E0E0), Color(0xFFBDBDBD))
                            )
                        }
                    )
                    .border(
                        width = 2.5.dp,
                        color = if (isUnlocked) ComicInkBlack else ComicInkBlack.copy(alpha = 0.35f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Text(text = badge.badgeEmoji, fontSize = 34.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = ComicInkBlack.copy(alpha = 0.45f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            // 2. Thông tin danh hiệu & Lời cà khịa
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = badge.title,
                    fontFamily = ComicFontFamily,
                    fontSize = 15.sp,
                    color = if (isUnlocked) ComicInkBlack else ComicInkBlack.copy(alpha = 0.5f),
                    lineHeight = 18.sp
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = if (isUnlocked) "\"${badge.description}\"" else "Mở khóa khi đạt ${String.format(Locale.US, "%,.0f", badge.requiredMeters)}m",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.5.sp,
                    color = if (isUnlocked) ComicInkBlack.copy(alpha = 0.75f) else ComicInkBlack.copy(alpha = 0.45f),
                    lineHeight = 15.sp
                )

                if (isUnlocked) {
                    Spacer(Modifier.height(6.dp))
                    // Nút xem / chia sẻ bằng khen
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ComicOrange.copy(alpha = 0.2f))
                            .border(1.5.dp, ComicOrange, RoundedCornerShape(8.dp))
                            .clickable(onClick = onViewCertificate)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MilitaryTech,
                            contentDescription = null,
                            tint = ComicInkBlack,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = " XEM BẰNG KHEN",
                            fontFamily = ComicFontFamily,
                            fontSize = 11.sp,
                            color = ComicInkBlack
                        )
                    }
                }
            }
        }
    }
}

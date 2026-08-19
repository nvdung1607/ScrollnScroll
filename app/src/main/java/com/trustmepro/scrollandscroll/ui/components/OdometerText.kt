package com.trustmepro.scrollandscroll.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

/**
 * Đồng hồ nhảy số kiểu cơ học (Odometer) hiển thị số mét chuẩn Material Design 3
 */
@Composable
fun OdometerText(
    meters: Double,
    modifier: Modifier = Modifier
) {
    val formatted = String.format(Locale.US, "%,.1f", meters)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "TỔNG KHOẢNG CÁCH ĐÃ CUỘN",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            formatted.forEach { char ->
                AnimatedContent(
                    targetState = char,
                    transitionSpec = {
                        slideInVertically { height -> height } togetherWith
                                slideOutVertically { height -> -height }
                    },
                    label = "odometerDigit"
                ) { targetChar ->
                    if (targetChar.isDigit()) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 1.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 5.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = targetChar.toString(),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 34.sp
                            )
                        }
                    } else {
                        Text(
                            text = targetChar.toString(),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 32.sp,
                            modifier = Modifier.padding(horizontal = 1.dp)
                        )
                    }
                }
            }

            Text(
                text = " m",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 3.dp, start = 4.dp)
            )
        }
    }
}

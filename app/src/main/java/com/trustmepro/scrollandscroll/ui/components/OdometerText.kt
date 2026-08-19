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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.trustmepro.scrollandscroll.ui.theme.ComicFontFamily
import java.util.Locale

/**
 * Đồng hồ nhảy số kiểu cơ học (Mechanical Odometer Counter) chuẩn phong cách hoạt hình Ảnh 2
 */
@Composable
fun ComicOdometerDisplay(
    meters: Double,
    modifier: Modifier = Modifier
) {
    val formattedNumber = String.format(Locale.US, "%,.1f", meters)
    val kmText = String.format(Locale.US, "(~ %.2f km)", meters / 1000.0)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tiêu đề nhãn trên
        Text(
            text = "TỔNG KHOẢNG CÁCH",
            fontFamily = ComicFontFamily,
            fontWeight = FontWeight.Black,
            fontSize = 12.5.sp,
            letterSpacing = 0.5.sp,
            color = ComicInkBlack,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(4.dp))

        // Hộp hiển thị số cơ học màu đen retro
        Box(
            modifier = Modifier
                .shadow(3.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2C2C2C),
                            Color(0xFF181818),
                            Color(0xFF101010),
                            Color(0xFF222222)
                        )
                    )
                )
                .border(2.dp, ComicInkBlack, RoundedCornerShape(8.dp))
                .padding(horizontal = 6.dp, vertical = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                formattedNumber.forEach { char ->
                    if (char.isDigit()) {
                        MechanicalDigitDrum(digit = char)
                    } else {
                        Text(
                            text = char.toString(),
                            fontFamily = ComicFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 1.dp)
                        )
                    }
                }

                Spacer(Modifier.width(5.dp))

                Text(
                    text = "MET",
                    fontFamily = ComicFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    letterSpacing = 0.5.sp,
                    color = Color.White
                )
            }
        }

        Spacer(Modifier.height(3.dp))

        // Dòng quy đổi km
        Text(
            text = kmText,
            fontFamily = ComicFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 11.5.sp,
            color = ComicInkBlack.copy(alpha = 0.85f),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Một ô trống số cơ học có rãnh phản chiếu ở giữa (Mechanical Drum Slot)
 */
@Composable
private fun MechanicalDigitDrum(digit: Char) {
    Box(
        modifier = Modifier
            .padding(horizontal = 0.5.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF383838),
                        Color(0xFF202020),
                        Color(0xFF151515),
                        Color(0xFF2E2E2E)
                    )
                )
            )
            .border(0.8.dp, Color(0xFF4A4A4A), RoundedCornerShape(3.dp))
            .padding(horizontal = 2.5.dp, vertical = 0.5.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = digit,
            transitionSpec = {
                slideInVertically { height -> height } togetherWith
                        slideOutVertically { height -> -height }
            },
            label = "drumAnim"
        ) { targetDigit ->
            Text(
                text = targetDigit.toString(),
                fontFamily = ComicFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        // Rãnh phản chiếu ngang nhẹ ở tâm trống cơ học
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

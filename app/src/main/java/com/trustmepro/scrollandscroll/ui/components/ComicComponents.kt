package com.trustmepro.scrollandscroll.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────────────────────────────────────
// Comic Design System — Color Palette
// ─────────────────────────────────────────────────────────────────────────────

/** Bộ màu sắc nhất quán cho toàn bộ giao diện Comic Pop-Art */
val ComicInkBlack = Color(0xFF1E1B18)
val ComicYellow   = Color(0xFFFFD54F)
val ComicGold     = Color(0xFFFFCA28)
val ComicGreen    = Color(0xFF66BB6A)
val ComicOrange   = Color(0xFFFF8A65)
val ComicCyan     = Color(0xFF80DEEA)
val ComicGray     = Color(0xFFBDBDBD)    // Nút Setting
val ComicLightYellow = Color(0xFFFFECB3) // Màu nền nhạt

// ─────────────────────────────────────────────────────────────────────────────
// ComicCircleButton — Nút tròn Pop-Art với viền đen, bóng 3D và label
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Nút bấm tròn phong cách truyện tranh (Comic Pop-Art).
 * Khi nhấn: hiệu ứng bóng nhẹ xuống và bóng đổ thu lại.
 *
 * @param icon       Icon hiển thị trong nút
 * @param label      Nhãn text bên dưới nút (hỗ trợ xuống hàng bằng \n)
 * @param containerColor Màu nền nút
 * @param onClick    Sự kiện khi nhấn
 * @param size       Đường kính nút (mặc định 54.dp)
 */
@Composable
fun ComicCircleButton(
    icon: ImageVector,
    label: String,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 54.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .offset(y = if (isPressed) 3.dp else 0.dp)
                .shadow(
                    elevation = if (isPressed) 1.dp else 6.dp,
                    shape = CircleShape,
                    spotColor = ComicInkBlack.copy(alpha = 0.55f)
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            lighten(containerColor, 0.18f),
                            containerColor
                        )
                    )
                )
                .border(width = 2.5.dp, color = ComicInkBlack, shape = CircleShape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.5f)),
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = ComicInkBlack,
                modifier = Modifier.size(size * 0.52f)
            )
        }

        if (label.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))

            // Label với bóng trắng và nét đen (giúp đọc được trên mọi nền)
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = label,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontSize = 11.sp,
                    lineHeight = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .offset(x = 1.dp, y = 1.dp)
                        .widthIn(max = 66.dp)
                )
                Text(
                    text = label,
                    fontWeight = FontWeight.ExtraBold,
                    color = ComicInkBlack,
                    fontSize = 11.sp,
                    lineHeight = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 66.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ComicCard — Thẻ nội dung HUD với viền đen đậm và bóng đổ
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Thẻ phong cách truyện tranh: nền trắng, viền đen 3dp, góc bo 18dp, bóng đổ.
 * Dùng làm HUD card hiển thị odometer và thống kê.
 */
@Composable
fun ComicCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(elevation = 10.dp, shape = RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor)
            .border(width = 3.dp, color = ComicInkBlack, shape = RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        content()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helper nội bộ
// ─────────────────────────────────────────────────────────────────────────────

/** Làm sáng một màu lên theo hệ số [amount] ∈ [0..1] */
private fun lighten(color: Color, amount: Float): Color = Color(
    red   = (color.red   + (1f - color.red)   * amount).coerceIn(0f, 1f),
    green = (color.green + (1f - color.green) * amount).coerceIn(0f, 1f),
    blue  = (color.blue  + (1f - color.blue)  * amount).coerceIn(0f, 1f),
    alpha = color.alpha
)

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────────────────────────────────────
// Comic Pop-Art Color Tokens (Khớp 100% hình mẫu gốc)
// ─────────────────────────────────────────────────────────────────────────────

val ComicInkBlack = Color(0xFF1B1B1B)
val ComicYellow   = Color(0xFFFFD54F)
val ComicGold     = Color(0xFFFFC107)
val ComicGreen    = Color(0xFF81C784)
val ComicOrange   = Color(0xFFFF8A65)
val ComicRedOrange= Color(0xFFFF5722)
val ComicCyan     = Color(0xFF80DEEA)
val ComicCardBg   = Color(0xFFFFFFFF)
val ComicTileBlue = Color(0xFFB3E5FC)

// ─────────────────────────────────────────────────────────────────────────────
// ComicCircleButton — Nút bấm tròn phong cách hoạt hình Pop-Art
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Nút bấm tròn phong cách Pop-Art với viền đen dày, bóng đổ 3D và nhãn chữ nổi bật
 */
@Composable
fun ComicCircleButton(
    icon: ImageVector,
    label: String,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Nút tròn có viền đen đậm và bóng đổ
        Box(
            modifier = Modifier
                .offset(y = if (isPressed) 3.dp else 0.dp)
                .size(size)
                .shadow(
                    elevation = if (isPressed) 1.dp else 6.dp,
                    shape = CircleShape,
                    spotColor = ComicInkBlack.copy(alpha = 0.5f)
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            containerColor.copy(alpha = 0.95f),
                            containerColor,
                            darken(containerColor, 0.12f)
                        )
                    )
                )
                .border(width = 3.dp, color = ComicInkBlack, shape = CircleShape)
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
                modifier = Modifier.size(size * 0.54f)
            )
        }

        if (label.isNotEmpty()) {
            Spacer(modifier = Modifier.height(3.dp))

            // Nhãn chữ viền kép (trắng + đen) sắc nét trên mọi nền gạch/tường
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = label,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 11.5.sp,
                    lineHeight = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .offset(x = 1.2.dp, y = 1.2.dp)
                        .widthIn(max = 72.dp)
                )
                Text(
                    text = label,
                    fontWeight = FontWeight.Black,
                    color = ComicInkBlack,
                    fontSize = 11.5.sp,
                    lineHeight = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(max = 72.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ComicCard — Thẻ HUD viền đen dày 3.5dp phong cách Pop-Art
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ComicCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = ComicCardBg,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp), spotColor = ComicInkBlack.copy(alpha = 0.4f))
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .border(width = 3.5.dp, color = ComicInkBlack, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        content()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Comic3DWordmark — Tiêu đề chữ 3D "SCROLL & SCROLL" siêu đẹp chuẩn hoạt hình
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun Comic3DWordmark(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dòng 1: "SCROLL"
        Comic3DText(text = "SCROLL", fontSize = 38.sp)

        // Dòng 2: Ký tự "&" nằm đè ở giữa 2 chữ SCROLL
        Box(
            modifier = Modifier.offset(y = (-4).dp),
            contentAlignment = Alignment.Center
        ) {
            // Nền tròn trắng nhỏ viền đen chứa chữ &
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.5.dp, ComicInkBlack, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "&",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = ComicInkBlack,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Dòng 3: "SCROLL"
        Comic3DText(
            text = "SCROLL",
            fontSize = 38.sp,
            modifier = Modifier.offset(y = (-6).dp)
        )

        // Huy hiệu banner viền đen: "CUỘN GIẤY VỆ SINH VÔ TẬN"
        Box(
            modifier = Modifier
                .offset(y = (-2).dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ComicInkBlack)
                .border(1.5.dp, ComicInkBlack, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 3.dp)
        ) {
            Text(
                text = "CUỘN GIẤY VỆ SINH VÔ TẬN",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.8.sp,
                color = Color(0xFFFFD54F),
                fontSize = 11.5.sp
            )
        }
    }
}

/**
 * Một khối chữ 3D Pop-Art với viền đen dày, bóng đổ 3D và gradient màu cam-đỏ nổi bật
 */
@Composable
fun Comic3DText(
    text: String,
    fontSize: TextUnit = 36.sp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Lớp 1: Bóng đổ 3D đen phía dưới
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            color = ComicInkBlack,
            modifier = Modifier.offset(x = 3.dp, y = 4.5.dp)
        )
        // Lớp 2: Viền đen bao quanh chữ (trái, phải, trên)
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            color = ComicInkBlack,
            modifier = Modifier.offset(x = (-1.5).dp, y = (-1.5).dp)
        )
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            color = ComicInkBlack,
            modifier = Modifier.offset(x = 1.5.dp, y = (-1.5).dp)
        )
        // Lớp 3: Màu chữ chính cam-đỏ rực rỡ
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            color = Color(0xFFFF5722)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers màu sắc
// ─────────────────────────────────────────────────────────────────────────────

private fun darken(color: Color, amount: Float): Color = Color(
    red = (color.red * (1f - amount)).coerceIn(0f, 1f),
    green = (color.green * (1f - amount)).coerceIn(0f, 1f),
    blue = (color.blue * (1f - amount)).coerceIn(0f, 1f),
    alpha = color.alpha
)

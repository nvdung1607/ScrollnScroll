package com.trustmepro.scrollandscroll.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustmepro.scrollandscroll.ui.theme.ComicFontFamily
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ─────────────────────────────────────────────────────────────────────────────
// Comic Pop-Art Color Tokens
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
// Custom Comic Hand-Drawn Icons (Chuẩn đồ họa hoạt hình Ảnh 2)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Biểu tượng Bánh Răng Hoạt Hình (Setting)
 */
@Composable
fun ComicGearIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    strokeColor: Color = ComicInkBlack
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val rOut = w * 0.42f
        val rIn = w * 0.30f
        val numTeeth = 6

        val gearPath = Path()
        for (i in 0 until numTeeth) {
            val angleStep = (2 * PI / numTeeth).toFloat()
            val startAngle = i * angleStep
            val a1 = startAngle - angleStep * 0.18f
            val a2 = startAngle - angleStep * 0.12f
            val a3 = startAngle + angleStep * 0.12f
            val a4 = startAngle + angleStep * 0.18f

            val p1 = Offset(cx + rIn * cos(a1), cy + rIn * sin(a1))
            val p2 = Offset(cx + rOut * cos(a2), cy + rOut * sin(a2))
            val p3 = Offset(cx + rOut * cos(a3), cy + rOut * sin(a3))
            val p4 = Offset(cx + rIn * cos(a4), cy + rIn * sin(a4))

            if (i == 0) gearPath.moveTo(p1.x, p1.y) else gearPath.lineTo(p1.x, p1.y)
            gearPath.lineTo(p2.x, p2.y)
            gearPath.lineTo(p3.x, p3.y)
            gearPath.lineTo(p4.x, p4.y)
        }
        gearPath.close()

        drawPath(gearPath, color = color)
        drawPath(gearPath, color = strokeColor, style = Stroke(width = w * 0.08f, join = StrokeJoin.Round))

        // Lỗ tròn trung tâm
        val rHole = w * 0.14f
        drawCircle(color = ComicCyan, radius = rHole, center = Offset(cx, cy))
        drawCircle(color = strokeColor, radius = rHole, center = Offset(cx, cy), style = Stroke(width = w * 0.08f))
    }
}

/**
 * Biểu tượng Cúp Vàng Hoạt Hình có Ngôi Sao (Bảng Xếp Hạng)
 */
@Composable
fun ComicTrophyIcon(
    modifier: Modifier = Modifier,
    cupColor: Color = Color(0xFFFFD54F),
    strokeColor: Color = ComicInkBlack
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = w * 0.075f

        // 1. Quai cúp 2 bên
        val handlePath = Path().apply {
            moveTo(w * 0.28f, h * 0.25f)
            cubicTo(w * 0.04f, h * 0.22f, w * 0.04f, h * 0.52f, w * 0.28f, h * 0.50f)
            moveTo(w * 0.72f, h * 0.25f)
            cubicTo(w * 0.96f, h * 0.22f, w * 0.96f, h * 0.52f, w * 0.72f, h * 0.50f)
        }
        drawPath(handlePath, color = strokeColor, style = Stroke(width = strokeW * 1.1f, cap = StrokeCap.Round))

        // 2. Thân cúp chính
        val cupPath = Path().apply {
            moveTo(w * 0.24f, h * 0.18f)
            lineTo(w * 0.76f, h * 0.18f)
            lineTo(w * 0.72f, h * 0.46f)
            cubicTo(w * 0.70f, h * 0.64f, w * 0.58f, h * 0.68f, w * 0.50f, h * 0.68f)
            cubicTo(w * 0.42f, h * 0.68f, w * 0.30f, h * 0.64f, w * 0.28f, h * 0.46f)
            close()
        }
        drawPath(cupPath, color = cupColor)
        drawPath(cupPath, color = strokeColor, style = Stroke(width = strokeW, join = StrokeJoin.Round))

        // 3. Chân cúp & Đế
        val stemPath = Path().apply {
            moveTo(w * 0.44f, h * 0.68f)
            lineTo(w * 0.56f, h * 0.68f)
            lineTo(w * 0.56f, h * 0.78f)
            lineTo(w * 0.44f, h * 0.78f)
            close()
        }
        drawPath(stemPath, color = cupColor)
        drawPath(stemPath, color = strokeColor, style = Stroke(width = strokeW))

        val baseRect = Path().apply {
            moveTo(w * 0.30f, h * 0.78f)
            lineTo(w * 0.70f, h * 0.78f)
            lineTo(w * 0.74f, h * 0.88f)
            lineTo(w * 0.26f, h * 0.88f)
            close()
        }
        drawPath(baseRect, color = Color(0xFFFFB300))
        drawPath(baseRect, color = strokeColor, style = Stroke(width = strokeW, join = StrokeJoin.Round))

        // 4. Ngôi sao nhỏ màu trắng trên cúp
        drawStar(cx = w * 0.50f, cy = h * 0.38f, rOut = w * 0.11f, rIn = w * 0.05f, color = Color.White, strokeColor = strokeColor)
    }
}

/**
 * Biểu tượng Móc Áo Hoạt Hình (Tủ Đồ Skin)
 */
@Composable
fun ComicHangerIcon(
    modifier: Modifier = Modifier,
    strokeColor: Color = ComicInkBlack
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = w * 0.085f

        // Móc câu phía trên
        val hookPath = Path().apply {
            moveTo(w * 0.46f, h * 0.42f)
            cubicTo(w * 0.46f, h * 0.22f, w * 0.62f, h * 0.16f, w * 0.54f, h * 0.28f)
            cubicTo(w * 0.50f, h * 0.34f, w * 0.50f, h * 0.42f, w * 0.50f, h * 0.46f)
        }
        drawPath(hookPath, color = strokeColor, style = Stroke(width = strokeW, cap = StrokeCap.Round))

        // Khung tam giác móc áo
        val framePath = Path().apply {
            moveTo(w * 0.50f, h * 0.46f)
            lineTo(w * 0.85f, h * 0.72f)
            cubicTo(w * 0.88f, h * 0.78f, w * 0.82f, h * 0.82f, w * 0.78f, h * 0.82f)
            lineTo(w * 0.22f, h * 0.82f)
            cubicTo(w * 0.18f, h * 0.82f, w * 0.12f, h * 0.78f, w * 0.15f, h * 0.72f)
            close()
        }
        drawPath(framePath, color = strokeColor, style = Stroke(width = strokeW, join = StrokeJoin.Round, cap = StrokeCap.Round))
    }
}

/**
 * Biểu tượng Loa Phát Sóng Hoạt Hình (ASMR Sound)
 */
@Composable
fun ComicSpeakerIcon(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    strokeColor: Color = ComicInkBlack
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = w * 0.08f

        // Thân loa
        val speakerPath = Path().apply {
            moveTo(w * 0.18f, h * 0.38f)
            lineTo(w * 0.34f, h * 0.38f)
            lineTo(w * 0.56f, h * 0.18f)
            lineTo(w * 0.56f, h * 0.82f)
            lineTo(w * 0.34f, h * 0.62f)
            lineTo(w * 0.18f, h * 0.62f)
            close()
        }
        drawPath(speakerPath, color = color)
        drawPath(speakerPath, color = strokeColor, style = Stroke(width = strokeW, join = StrokeJoin.Round))

        // Sóng âm thanh 1
        val wave1 = Path().apply {
            moveTo(w * 0.68f, h * 0.34f)
            cubicTo(w * 0.76f, h * 0.42f, w * 0.76f, h * 0.58f, w * 0.68f, h * 0.66f)
        }
        drawPath(wave1, color = strokeColor, style = Stroke(width = strokeW, cap = StrokeCap.Round))

        // Sóng âm thanh 2
        val wave2 = Path().apply {
            moveTo(w * 0.80f, h * 0.24f)
            cubicTo(w * 0.94f, h * 0.38f, w * 0.94f, h * 0.62f, w * 0.80f, h * 0.76f)
        }
        drawPath(wave2, color = strokeColor, style = Stroke(width = strokeW, cap = StrokeCap.Round))
    }
}

/**
 * Biểu tượng Cuộn Văn Bằng Chứng Chỉ Hoạt Hình (Bằng Khen Của Tôi - Nút trên)
 */
@Composable
fun ComicScrollCertificateIcon(
    modifier: Modifier = Modifier,
    paperColor: Color = Color(0xFFFFF9C4),
    strokeColor: Color = ComicInkBlack
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = w * 0.075f

        // Cuộn giấy mở ra
        val scrollPath = Path().apply {
            moveTo(w * 0.24f, h * 0.22f)
            cubicTo(w * 0.30f, h * 0.16f, w * 0.76f, h * 0.16f, w * 0.82f, h * 0.22f)
            lineTo(w * 0.78f, h * 0.76f)
            cubicTo(w * 0.72f, h * 0.82f, w * 0.26f, h * 0.82f, w * 0.20f, h * 0.76f)
            close()
        }
        drawPath(scrollPath, color = paperColor)
        drawPath(scrollPath, color = strokeColor, style = Stroke(width = strokeW, join = StrokeJoin.Round))

        // Hai mép cuộn tròn trên và dưới
        drawArc(
            color = strokeColor,
            startAngle = 140f,
            sweepAngle = 260f,
            useCenter = false,
            topLeft = Offset(w * 0.14f, h * 0.15f),
            size = Size(w * 0.20f, h * 0.16f),
            style = Stroke(width = strokeW)
        )
        drawArc(
            color = strokeColor,
            startAngle = -40f,
            sweepAngle = 260f,
            useCenter = false,
            topLeft = Offset(w * 0.68f, h * 0.68f),
            size = Size(w * 0.20f, h * 0.16f),
            style = Stroke(width = strokeW)
        )

        // Các dòng chữ mô phỏng bên trong bằng khen
        drawLine(strokeColor.copy(alpha = 0.5f), Offset(w * 0.36f, h * 0.36f), Offset(w * 0.66f, h * 0.36f), strokeWidth = strokeW * 0.7f, cap = StrokeCap.Round)
        drawLine(strokeColor.copy(alpha = 0.5f), Offset(w * 0.36f, h * 0.48f), Offset(w * 0.66f, h * 0.48f), strokeWidth = strokeW * 0.7f, cap = StrokeCap.Round)

        // Con dấu đỏ/ruy băng
        drawCircle(color = Color(0xFFFF5252), radius = w * 0.08f, center = Offset(w * 0.52f, h * 0.62f))
        drawCircle(color = strokeColor, radius = w * 0.08f, center = Offset(w * 0.52f, h * 0.62f), style = Stroke(width = strokeW * 0.7f))
    }
}

/**
 * Biểu tượng Bằng Khen Dạng Văn Bằng Hoạt Hình (Bằng Khen Của Tôi - Nút dưới)
 */
@Composable
fun ComicCertificateDocIcon(
    modifier: Modifier = Modifier,
    paperColor: Color = Color(0xFFFFF9C4),
    strokeColor: Color = ComicInkBlack
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = w * 0.075f

        // Tờ giấy bằng khen
        val docPath = Path().apply {
            moveTo(w * 0.22f, h * 0.16f)
            lineTo(w * 0.65f, h * 0.16f)
            lineTo(w * 0.78f, h * 0.30f)
            lineTo(w * 0.78f, h * 0.84f)
            lineTo(w * 0.22f, h * 0.84f)
            close()
        }
        drawPath(docPath, color = paperColor)
        drawPath(docPath, color = strokeColor, style = Stroke(width = strokeW, join = StrokeJoin.Round))

        // Góc gấp
        val foldPath = Path().apply {
            moveTo(w * 0.65f, h * 0.16f)
            lineTo(w * 0.65f, h * 0.30f)
            lineTo(w * 0.78f, h * 0.30f)
            close()
        }
        drawPath(foldPath, color = Color(0xFFFFE082))
        drawPath(foldPath, color = strokeColor, style = Stroke(width = strokeW * 0.8f, join = StrokeJoin.Round))

        // Dòng chữ mô phỏng
        drawLine(strokeColor.copy(alpha = 0.5f), Offset(w * 0.32f, h * 0.38f), Offset(w * 0.58f, h * 0.38f), strokeWidth = strokeW * 0.7f, cap = StrokeCap.Round)
        drawLine(strokeColor.copy(alpha = 0.5f), Offset(w * 0.32f, h * 0.48f), Offset(w * 0.68f, h * 0.48f), strokeWidth = strokeW * 0.7f, cap = StrokeCap.Round)
        drawLine(strokeColor.copy(alpha = 0.5f), Offset(w * 0.32f, h * 0.58f), Offset(w * 0.68f, h * 0.58f), strokeWidth = strokeW * 0.7f, cap = StrokeCap.Round)

        // Dải ruy băng / Huân chương nhỏ
        drawCircle(color = Color(0xFFFFD54F), radius = w * 0.08f, center = Offset(w * 0.50f, h * 0.72f))
        drawCircle(color = strokeColor, radius = w * 0.08f, center = Offset(w * 0.50f, h * 0.72f), style = Stroke(width = strokeW * 0.7f))
    }
}

private fun DrawScope.drawStar(cx: Float, cy: Float, rOut: Float, rIn: Float, color: Color, strokeColor: Color) {
    val path = Path()
    val numPoints = 5
    for (i in 0 until numPoints * 2) {
        val r = if (i % 2 == 0) rOut else rIn
        val angle = (i * PI / numPoints - PI / 2).toFloat()
        val x = cx + r * cos(angle)
        val y = cy + r * sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color = color)
    drawPath(path, color = strokeColor, style = Stroke(width = rOut * 0.35f, join = StrokeJoin.Round))
}

// ─────────────────────────────────────────────────────────────────────────────
// ComicCircleButton — Nút bấm tròn phong cách hoạt hình Pop-Art viền đen 3.5dp
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ComicCircleButton(
    label: String,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    iconContent: @Composable () -> Unit
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
                    elevation = if (isPressed) 1.dp else 5.dp,
                    shape = CircleShape,
                    spotColor = ComicInkBlack.copy(alpha = 0.5f)
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            containerColor.copy(alpha = 0.95f),
                            containerColor,
                            darken(containerColor, 0.15f)
                        )
                    )
                )
                .border(width = 3.5.dp, color = ComicInkBlack, shape = CircleShape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.5f)),
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.size(size * 0.60f), contentAlignment = Alignment.Center) {
                iconContent()
            }
        }

        if (label.isNotEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                fontFamily = ComicFontFamily,
                fontWeight = FontWeight.Black,
                color = ComicInkBlack,
                fontSize = 11.sp,
                lineHeight = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 72.dp)
            )
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
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(18.dp), spotColor = ComicInkBlack.copy(alpha = 0.45f))
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor)
            .border(width = 3.5.dp, color = ComicInkBlack, shape = RoundedCornerShape(18.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        content()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Comic3DWordmark — Tiêu đề chữ 3D "SCROLL & SCROLL" siêu đẹp chuẩn hoạt hình Ảnh 2
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
        Comic3DBubbleText(text = "SCROLL", fontSize = 38.sp)

        // Dòng 2: Ký tự "&" nằm đè ở giữa 2 chữ SCROLL
        Box(
            modifier = Modifier.offset(y = (-6).dp),
            contentAlignment = Alignment.Center
        ) {
            // Nền tròn trắng nhỏ viền đen chứa chữ &
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .shadow(3.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.8.dp, ComicInkBlack, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "&",
                    fontFamily = ComicFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = ComicInkBlack,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Dòng 3: "SCROLL"
        Comic3DBubbleText(
            text = "SCROLL",
            fontSize = 38.sp,
            modifier = Modifier.offset(y = (-12).dp)
        )

        // Phụ đề: — CUỘN GIẤY VỆ SINH VÔ TẬN — (Chuẩn xác 100% Ảnh 2)
        Row(
            modifier = Modifier
                .offset(y = (-8).dp)
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .width(10.dp)
                    .height(2.5.dp)
                    .background(ComicInkBlack, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "CUỘN GIẤY VỆ SINH VÔ TẬN",
                fontFamily = ComicFontFamily,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.2.sp,
                color = ComicInkBlack,
                fontSize = 12.5.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .width(10.dp)
                    .height(2.5.dp)
                    .background(ComicInkBlack, RoundedCornerShape(2.dp))
            )
        }
    }
}



/**
 * Một khối chữ 3D Pop-Art Bubble với viền đen dày, bóng đổ 3D và gradient màu cam-đỏ nổi bật
 */
@Composable
fun Comic3DBubbleText(
    text: String,
    fontSize: TextUnit = 40.sp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Lớp 1: Khối 3D màu đen sâu phía dưới (Bottom-Right Extrusion)
        for (step in 1..4) {
            Text(
                text = text,
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                fontFamily = ComicFontFamily,
                color = ComicInkBlack,
                modifier = Modifier.offset(x = 0.dp, y = (step * 1.5).dp)
            )
        }

        // Lớp 2: Viền đen toàn bộ xung quanh chữ (8 hướng)
        val offsets = listOf(
            Offset(-2f, -2f), Offset(0f, -2.5f), Offset(2f, -2f),
            Offset(-2.5f, 0f), Offset(2.5f, 0f),
            Offset(-2f, 2f), Offset(0f, 2.5f), Offset(2f, 2f)
        )
        for (off in offsets) {
            Text(
                text = text,
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                fontFamily = ComicFontFamily,
                color = ComicInkBlack,
                modifier = Modifier.offset(x = off.x.dp, y = off.y.dp)
            )
        }

        // Lớp 3: Màu chữ gradient cam-đỏ rực rỡ
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            fontFamily = ComicFontFamily,
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


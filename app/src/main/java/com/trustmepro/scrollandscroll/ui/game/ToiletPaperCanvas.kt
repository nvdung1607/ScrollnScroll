package com.trustmepro.scrollandscroll.ui.game

import android.graphics.Paint as AndroidPaint
import android.graphics.Typeface
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.trustmepro.scrollandscroll.R
import com.trustmepro.scrollandscroll.data.model.SkinType
import com.trustmepro.scrollandscroll.ui.components.ComicInkBlack
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * ToiletPaperCanvas — Màn hình tương tác cuộn giấy 3D và dải giấy uốn lượn S-Curve.
 * Áp dụng trọn vẹn màu sắc, hoạt ảnh xoay, và con dấu họa tiết đặc trưng của từng loại Skin.
 */
@Composable
fun ToiletPaperCanvas(
    skin: SkinType,
    isOverdrive: Boolean,
    onScroll: (pixels: Float, velocity: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var rollRotationOffset by remember { mutableFloatStateOf(0f) }
    var paperScrollOffset by remember { mutableFloatStateOf(0f) }
    var currentVelocity by remember { mutableFloatStateOf(0f) }
    val flingAnim = remember { Animatable(0f) }
    val velocityTracker = remember { VelocityTracker() }

    // Nhịp rung/vẫy tự nhiên khi giấy bay trong không khí
    val infiniteTransition = rememberInfiniteTransition(label = "paperFlutter")
    val flutterPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flutter"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        velocityTracker.resetTracking()
                        coroutineScope.launch { flingAnim.stop() }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        velocityTracker.addPosition(change.uptimeMillis, change.position)

                        // Vuốt theo bất kỳ hướng nào (lên, xuống, trái, phải, chéo) đều tính số mét cuộn
                        val deltaMagnitude = kotlin.math.hypot(dragAmount.x, dragAmount.y)
                        if (deltaMagnitude > 0f) {
                            rollRotationOffset += deltaMagnitude * 0.55f
                            paperScrollOffset += deltaMagnitude
                            currentVelocity = deltaMagnitude * 35f
                            onScroll(deltaMagnitude, currentVelocity)
                        }
                    },
                    onDragEnd = {
                        val vx = velocityTracker.calculateVelocity().x
                        val vy = velocityTracker.calculateVelocity().y
                        val velocityMagnitude = kotlin.math.hypot(vx, vy)
                        currentVelocity = velocityMagnitude
                        if (velocityMagnitude > 200f) {
                            coroutineScope.launch {
                                var lastValue = 0f
                                flingAnim.snapTo(0f)
                                flingAnim.animateTo(
                                    targetValue = (velocityMagnitude * 0.75f).coerceAtMost(3800f),
                                    animationSpec = tween(durationMillis = 950, easing = FastOutSlowInEasing)
                                ) {
                                    val delta = this.value - lastValue
                                    if (delta > 0f) {
                                        rollRotationOffset += delta * 0.55f
                                        paperScrollOffset += delta
                                        currentVelocity = (targetValue - this.value) * 1.3f
                                        onScroll(delta, currentVelocity)
                                    }
                                    lastValue = this.value
                                }
                                currentVelocity = 0f
                            }
                        } else {
                            currentVelocity = 0f
                        }
                    }
                )
            }
    ) {
        // 1. Hình nền nhà vệ sinh hoạt hình 2D Comic
        Image(
            painter = painterResource(id = R.drawable.bg_bathroom_game),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Lớp Canvas tương tác vẽ Cuộn giấy 3D + Dải giấy S-Curve theo từng Skin
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height

            // Tọa độ trục cuộn giấy 3D khớp giá gỗ
            val rightCapCenter = Offset(canvasW * 0.630f, canvasH * 0.380f)
            val rightCapRx = canvasW * 0.115f
            val rightCapRy = canvasW * 0.126f

            val leftEndCenter = Offset(canvasW * 0.385f, canvasH * 0.392f)
            val leftCapRx = canvasW * 0.088f
            val leftCapRy = canvasW * 0.118f

            // A. Vẽ dải giấy S-Curve uốn lượn liên tục theo màu Skin và họa tiết Emoji trôi nổi
            drawContinuousSmoothPaperRibbon(
                canvasW = canvasW,
                canvasH = canvasH,
                scrollOffset = paperScrollOffset,
                flutterPhase = flutterPhase,
                velocity = currentVelocity,
                skin = skin,
                isOverdrive = isOverdrive
            )

            // B. Vẽ Cuộn Giấy Vệ Sinh 3D Đa Chiều khớp với khung gỗ
            drawFrameFittedPaperRoll(
                rightCenter = rightCapCenter,
                rightRx = rightCapRx,
                rightRy = rightCapRy,
                leftCenter = leftEndCenter,
                leftRx = leftCapRx,
                leftRy = leftCapRy,
                rotationOffset = rollRotationOffset,
                skin = skin
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Vẽ Cuộn Giấy 3D Khớp Hoàn Hảo Với Khung Gỗ Và Áp Dụng Skin
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawFrameFittedPaperRoll(
    rightCenter: Offset,
    rightRx: Float,
    rightRy: Float,
    leftCenter: Offset,
    leftRx: Float,
    leftRy: Float,
    rotationOffset: Float,
    skin: SkinType
) {
    val isDarkSkin = skin == SkinType.SPACE_GALAXY
    val inkColor = if (isDarkSkin) Color(0xFFE0E0E0) else ComicInkBlack

    // 1. Bóng đổ mềm dưới cuộn giấy lên tường gạch
    val shadowPath = Path().apply {
        moveTo(leftCenter.x - leftRx + 4f, leftCenter.y + 6f)
        lineTo(rightCenter.x + rightRx + 8f, rightCenter.y + 8f)
        lineTo(rightCenter.x + rightRx + 8f, rightCenter.y + rightRy + 16f)
        lineTo(leftCenter.x - leftRx + 4f, leftCenter.y + leftRy + 14f)
        close()
    }
    drawPath(shadowPath, color = ComicInkBlack.copy(alpha = 0.22f))

    // 2. Thân cuộn giấy hình trụ 3D nhuộm màu Skin
    val bodyPath = Path().apply {
        moveTo(leftCenter.x, leftCenter.y - leftRy)
        lineTo(rightCenter.x, rightCenter.y - rightRy)
        cubicTo(
            rightCenter.x + rightRx * 1.25f, rightCenter.y - rightRy * 0.5f,
            rightCenter.x + rightRx * 1.25f, rightCenter.y + rightRy * 0.5f,
            rightCenter.x, rightCenter.y + rightRy
        )
        lineTo(leftCenter.x, leftCenter.y + leftRy)
        cubicTo(
            leftCenter.x - leftRx * 1.25f, leftCenter.y + leftRy * 0.5f,
            leftCenter.x - leftRx * 1.25f, leftCenter.y - leftRy * 0.5f,
            leftCenter.x, leftCenter.y - leftRy
        )
        close()
    }

    val bodyBrush = Brush.verticalGradient(
        colors = listOf(
            skin.primaryColor,
            skin.primaryColor.copy(alpha = 0.95f),
            skin.accentColor.copy(alpha = 0.85f),
            skin.accentColor
        ),
        startY = leftCenter.y - leftRy,
        endY = leftCenter.y + leftRy
    )
    drawPath(bodyPath, brush = bodyBrush)

    // 3. ĐƯỜNG KẺ NÉT ĐỨT CONG ELIP CHUẨN XÁC THEO MẶT TRỤ 3D CUỘN GIẤY
    val seamSpacing = 55f
    val totalWidth = (rightCenter.x - leftCenter.x)
    val normRot = (rotationOffset % seamSpacing)
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(9f, 8f), 0f)

    for (k in -1..6) {
        val frac = ((k * seamSpacing + normRot) / totalWidth).coerceIn(-0.2f, 1.2f)
        if (frac in 0.04f..0.92f) {
            val cx = leftCenter.x + (rightCenter.x - leftCenter.x) * frac
            val cy = leftCenter.y + (rightCenter.y - leftCenter.y) * frac
            val curRx = leftRx + (rightRx - leftRx) * frac
            val curRy = leftRy + (rightRy - leftRy) * frac

            // Cung elip cong tròn theo mặt trụ thân cuộn
            val arcPath = Path().apply {
                moveTo(cx, cy - curRy)
                cubicTo(
                    cx - curRx * 1.25f, cy - curRy * 0.5f,
                    cx - curRx * 1.25f, cy + curRy * 0.5f,
                    cx, cy + curRy
                )
            }
            drawPath(
                path = arcPath,
                color = inkColor.copy(alpha = 0.50f),
                style = Stroke(width = 3.2f, pathEffect = dashEffect)
            )

            // In họa tiết Skin xoay tròn trên mặt cuộn giấy
            if (frac in 0.20f..0.80f && skin != SkinType.SCHOOL_CANTEEN) {
                drawIntoCanvas { canvas ->
                    val paint = AndroidPaint().apply {
                        textSize = 28f
                        textAlign = AndroidPaint.Align.CENTER
                        isAntiAlias = true
                    }
                    canvas.nativeCanvas.drawText(
                        skin.patternEmoji,
                        cx - curRx * 0.4f,
                        cy + 8f,
                        paint
                    )
                }
            }
        }
    }

    // 4. Viền mực đen Comic cho mép trên, mép dưới và mép trái thân cuộn
    val bodyOutlineTop = Path().apply {
        moveTo(leftCenter.x, leftCenter.y - leftRy)
        lineTo(rightCenter.x, rightCenter.y - rightRy)
    }
    val bodyOutlineBot = Path().apply {
        moveTo(leftCenter.x, leftCenter.y + leftRy)
        lineTo(rightCenter.x, rightCenter.y + rightRy)
    }
    val bodyOutlineLeft = Path().apply {
        moveTo(leftCenter.x, leftCenter.y - leftRy)
        cubicTo(
            leftCenter.x - leftRx * 1.25f, leftCenter.y - leftRy * 0.5f,
            leftCenter.x - leftRx * 1.25f, leftCenter.y + leftRy * 0.5f,
            leftCenter.x, leftCenter.y + leftRy
        )
    }
    drawPath(bodyOutlineTop, color = ComicInkBlack, style = Stroke(width = 4.5f, cap = StrokeCap.Round))
    drawPath(bodyOutlineBot, color = ComicInkBlack, style = Stroke(width = 4.5f, cap = StrokeCap.Round))
    drawPath(bodyOutlineLeft, color = ComicInkBlack, style = Stroke(width = 4.5f, cap = StrokeCap.Round))

    // 5. Mặt tròn bên phải cuộn giấy (Right Ellipse Cap)
    val rightCapPath = Path().apply {
        moveTo(rightCenter.x, rightCenter.y - rightRy)
        cubicTo(
            rightCenter.x - rightRx * 1.333f, rightCenter.y - rightRy,
            rightCenter.x - rightRx * 1.333f, rightCenter.y + rightRy,
            rightCenter.x, rightCenter.y + rightRy
        )
        cubicTo(
            rightCenter.x + rightRx * 1.333f, rightCenter.y + rightRy,
            rightCenter.x + rightRx * 1.333f, rightCenter.y - rightRy,
            rightCenter.x, rightCenter.y - rightRy
        )
        close()
    }
    drawPath(
        path = rightCapPath,
        brush = Brush.radialGradient(
            colors = listOf(
                skin.primaryColor,
                skin.primaryColor.copy(alpha = 0.9f),
                skin.accentColor
            ),
            center = rightCenter,
            radius = rightRy
        )
    )

    // A. Các vòng xoắn đồng tâm thể hiện nhiều lớp giấy quấn
    val numRings = 4
    for (r in 1..numRings) {
        val ringFraction = 0.35f + (r.toFloat() / (numRings + 1)) * 0.58f
        val rx = rightRx * ringFraction
        val ry = rightRy * ringFraction
        val ringPath = Path().apply {
            moveTo(rightCenter.x, rightCenter.y - ry)
            cubicTo(
                rightCenter.x - rx * 1.333f, rightCenter.y - ry,
                rightCenter.x - rx * 1.333f, rightCenter.y + ry,
                rightCenter.x, rightCenter.y + ry
            )
            cubicTo(
                rightCenter.x + rx * 1.333f, rightCenter.y + ry,
                rightCenter.x + rx * 1.333f, rightCenter.y - ry,
                rightCenter.x, rightCenter.y - ry
            )
            close()
        }
        drawPath(
            path = ringPath,
            color = inkColor.copy(alpha = 0.35f),
            style = Stroke(width = 2.0f)
        )
    }

    // B. Lõi bìa carton ở tâm
    val coreRx = rightRx * 0.35f
    val coreRy = rightRy * 0.35f
    val corePath = Path().apply {
        moveTo(rightCenter.x, rightCenter.y - coreRy)
        cubicTo(
            rightCenter.x - coreRx * 1.333f, rightCenter.y - coreRy,
            rightCenter.x - coreRx * 1.333f, rightCenter.y + coreRy,
            rightCenter.x, rightCenter.y + coreRy
        )
        cubicTo(
            rightCenter.x + coreRx * 1.333f, rightCenter.y + coreRy,
            rightCenter.x + coreRx * 1.333f, rightCenter.y - coreRy,
            rightCenter.x, rightCenter.y - coreRy
        )
        close()
    }
    drawPath(
        path = corePath,
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFD7CCC8), Color(0xFF8D6E63), Color(0xFF4E342E)),
            center = rightCenter,
            radius = coreRy
        )
    )
    drawPath(path = corePath, color = ComicInkBlack, style = Stroke(width = 3.5f))

    // C. Lỗ rỗng đen sâu ở giữa ống carton
    val holeRx = coreRx * 0.55f
    val holeRy = coreRy * 0.55f
    val holePath = Path().apply {
        moveTo(rightCenter.x, rightCenter.y - holeRy)
        cubicTo(
            rightCenter.x - holeRx * 1.333f, rightCenter.y - holeRy,
            rightCenter.x - holeRx * 1.333f, rightCenter.y + holeRy,
            rightCenter.x, rightCenter.y + holeRy
        )
        cubicTo(
            rightCenter.x + holeRx * 1.333f, rightCenter.y + holeRy,
            rightCenter.x + holeRx * 1.333f, rightCenter.y - holeRy,
            rightCenter.x, rightCenter.y - holeRy
        )
        close()
    }
    drawPath(path = holePath, color = ComicInkBlack)

    // D. Viền đen đậm bao quanh toàn bộ mặt tròn bên phải
    drawPath(path = rightCapPath, color = ComicInkBlack, style = Stroke(width = 4.5f))

    // 6. Vệt sáng phản chiếu Specular Highlight trên đỉnh cuộn giấy
    val specPath = Path().apply {
        moveTo(leftCenter.x + 8f, leftCenter.y - leftRy + 4f)
        lineTo(rightCenter.x - 8f, rightCenter.y - rightRy + 4f)
        lineTo(rightCenter.x - 8f, rightCenter.y - rightRy + 14f)
        lineTo(leftCenter.x + 8f, leftCenter.y - leftRy + 14f)
        close()
    }
    drawPath(specPath, color = Color.White.copy(alpha = 0.75f))

    // 7. 3 giọt nước bắn cong & 2 vệt gió xoay quanh cuộn giấy
    drawActionDropsAndLines(leftCenter = leftCenter, leftRx = leftRx, leftRy = leftRy)
}

// ─────────────────────────────────────────────────────────────────────────────
// Vẽ hiệu ứng hoạt hình: 3 giọt nước vung & 2 vệt xoay
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawActionDropsAndLines(
    leftCenter: Offset,
    leftRx: Float,
    leftRy: Float
) {
    val dropColor = Color(0xFF64B5F6)
    val dropOutline = ComicInkBlack

    val dropPositions = listOf(
        Offset(leftCenter.x - leftRx * 0.65f, leftCenter.y - leftRy * 1.10f) to 14f,
        Offset(leftCenter.x - leftRx * 0.30f, leftCenter.y - leftRy * 1.35f) to 18f,
        Offset(leftCenter.x + leftRx * 0.15f, leftCenter.y - leftRy * 1.45f) to 12f
    )

    for ((pos, sz) in dropPositions) {
        val dropPath = Path().apply {
            moveTo(pos.x, pos.y - sz * 0.6f)
            cubicTo(pos.x + sz * 0.6f, pos.y - sz * 0.2f, pos.x + sz * 0.5f, pos.y + sz * 0.6f, pos.x, pos.y + sz * 0.6f)
            cubicTo(pos.x - sz * 0.5f, pos.y + sz * 0.6f, pos.x - sz * 0.6f, pos.y - sz * 0.2f, pos.x, pos.y - sz * 0.6f)
            close()
        }
        drawPath(dropPath, color = dropColor)
        drawPath(dropPath, color = dropOutline, style = Stroke(width = 2.8f))
    }

    val arcPath1 = Path().apply {
        moveTo(leftCenter.x - leftRx * 1.15f, leftCenter.y - leftRy * 0.40f)
        cubicTo(
            leftCenter.x - leftRx * 1.35f, leftCenter.y,
            leftCenter.x - leftRx * 1.35f, leftCenter.y + leftRy * 0.35f,
            leftCenter.x - leftRx * 1.12f, leftCenter.y + leftRy * 0.60f
        )
    }
    val arcPath2 = Path().apply {
        moveTo(leftCenter.x - leftRx * 1.30f, leftCenter.y - leftRy * 0.20f)
        cubicTo(
            leftCenter.x - leftRx * 1.52f, leftCenter.y + leftRy * 0.1f,
            leftCenter.x - leftRx * 1.52f, leftCenter.y + leftRy * 0.35f,
            leftCenter.x - leftRx * 1.28f, leftCenter.y + leftRy * 0.50f
        )
    }

    drawPath(arcPath1, color = dropOutline, style = Stroke(width = 3f, cap = StrokeCap.Round))
    drawPath(arcPath2, color = dropOutline, style = Stroke(width = 2.5f, cap = StrokeCap.Round))
}

// ─────────────────────────────────────────────────────────────────────────────
// Vẽ Dải Giấy Uốn Lượn S-Curve Mềm Mại Xếp Lớp Nhuộm Màu Skin & Họa Tiết Trôi Nổi
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawContinuousSmoothPaperRibbon(
    canvasW: Float,
    canvasH: Float,
    scrollOffset: Float,
    flutterPhase: Float,
    velocity: Float,
    skin: SkinType,
    isOverdrive: Boolean
) {
    val speedFactor = (velocity / 1200f).coerceIn(0f, 2.0f)
    val wave1 = sin(flutterPhase) * (2.0f + speedFactor * 3.5f)
    val wave2 = cos(flutterPhase + 1.2f) * (2.0f + speedFactor * 4f)

    val strokeWidth = 4.5f
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 9f), 0f)

    val isDarkSkin = skin == SkinType.SPACE_GALAXY
    val paperColor = skin.primaryColor
    val foldColor = if (isOverdrive) Color(0xFFFFE082) else skin.accentColor
    val inkColor = if (isDarkSkin) Color(0xFFE0E0E0) else ComicInkBlack

    // ── CÁC TỌA ĐỘ ĐIỂM CHỐT LIỀN MẠCH GIỮA 3 ĐOẠN ──
    val p0_L = Offset(canvasW * 0.44f, canvasH * 0.445f)
    val p0_R = Offset(canvasW * 0.60f, canvasH * 0.440f)

    val p1_top = Offset(canvasW * 0.38f + wave1, canvasH * 0.580f)
    val p1_bot = Offset(canvasW * 0.22f + wave1, canvasH * 0.635f)

    val p2_top = Offset(canvasW * 0.72f + wave2, canvasH * 0.635f)
    val p2_bot = Offset(canvasW * 0.56f + wave2, canvasH * 0.705f)

    val p3_bot = Offset(canvasW * 0.25f, canvasH * 0.790f)
    val p3_top = Offset(canvasW * 0.50f, canvasH * 0.810f)

    // ── 1. ĐOẠN 3 (LỚP ĐÁY: TỪ KHÚC UỐN PHẢI ĐỔ VÀO ĐỐNG GIẤY SÀN) ──
    val shadow3 = Path().apply {
        moveTo(p2_top.x + 8f, p2_top.y + 10f)
        cubicTo(canvasW * 0.65f + 8f, canvasH * 0.73f + 10f, canvasW * 0.58f + 8f, canvasH * 0.78f + 10f, p3_top.x + 8f, p3_top.y + 10f)
        lineTo(p3_bot.x + 8f, p3_bot.y + 10f)
        cubicTo(canvasW * 0.36f + 8f, canvasH * 0.75f + 10f, canvasW * 0.44f + 8f, canvasH * 0.72f + 10f, p2_bot.x + 8f, p2_bot.y + 10f)
        close()
    }
    drawPath(shadow3, color = ComicInkBlack.copy(alpha = 0.14f))

    val seg3Path = Path().apply {
        moveTo(p2_top.x, p2_top.y)
        cubicTo(canvasW * 0.65f + wave2, canvasH * 0.730f, canvasW * 0.58f, canvasH * 0.780f, p3_top.x, p3_top.y)
        lineTo(p3_bot.x, p3_bot.y)
        cubicTo(canvasW * 0.36f, canvasH * 0.750f, canvasW * 0.44f + wave2, canvasH * 0.720f, p2_bot.x, p2_bot.y)
        cubicTo(
            p2_bot.x + (p2_top.x - p2_bot.x) * 0.35f, p2_bot.y - 8f,
            p2_bot.x + (p2_top.x - p2_bot.x) * 0.75f, p2_top.y + 12f,
            p2_top.x, p2_top.y
        )
        close()
    }
    drawPath(seg3Path, color = paperColor)
    drawPath(seg3Path, color = ComicInkBlack, style = Stroke(width = strokeWidth, join = StrokeJoin.Round, cap = StrokeCap.Round))

    // Đường nét đứt & họa tiết đoạn 3
    val seg3Dash = (scrollOffset % 120f) / 120f
    for (k in 0..1) {
        val t = (seg3Dash + k * 0.50f) % 1.0f
        if (t in 0.10f..0.90f) {
            val lx = p2_bot.x + (p3_bot.x - p2_bot.x) * t
            val ly = p2_bot.y + (p3_bot.y - p2_bot.y) * t
            val rx = p2_top.x + (p3_top.x - p2_top.x) * t
            val ry = p2_top.y + (p3_top.y - p2_top.y) * t
            drawLine(
                color = inkColor.copy(alpha = 0.55f),
                start = Offset(lx, ly),
                end = Offset(rx, ry),
                strokeWidth = 3f,
                pathEffect = dashEffect
            )

            // Con dấu Skin ở giữa đoạn giấy
            if (skin != SkinType.SCHOOL_CANTEEN) {
                drawIntoCanvas { canvas ->
                    val paint = AndroidPaint().apply {
                        textSize = 36f
                        textAlign = AndroidPaint.Align.CENTER
                        isAntiAlias = true
                    }
                    val mx = (lx + rx) / 2f
                    val my = (ly + ry) / 2f
                    canvas.nativeCanvas.drawText(skin.patternEmoji, mx, my + 10f, paint)
                }
            }
        }
    }

    // ── 2. ĐOẠN 2 (LỚP GIỮA: TỪ KHÚC UỐN TRÁI QUÉT QUA KHÚC UỐN PHẢI) ──
    val rightLoop = Path().apply {
        moveTo(p2_top.x, p2_top.y)
        cubicTo(
            p2_top.x + 22f, p2_top.y + 16f,
            p2_bot.x + 28f, p2_bot.y - 8f,
            p2_bot.x, p2_bot.y
        )
        cubicTo(
            p2_bot.x + (p2_top.x - p2_bot.x) * 0.35f, p2_bot.y - 8f,
            p2_bot.x + (p2_top.x - p2_bot.x) * 0.75f, p2_top.y + 12f,
            p2_top.x, p2_top.y
        )
        close()
    }
    drawPath(rightLoop, color = foldColor)
    drawPath(rightLoop, color = ComicInkBlack, style = Stroke(width = strokeWidth, join = StrokeJoin.Round))

    val shadow2 = Path().apply {
        moveTo(p1_top.x + 8f, p1_top.y + 10f)
        cubicTo(canvasW * 0.50f + 8f, canvasH * 0.57f + 10f, canvasW * 0.62f + 8f, canvasH * 0.59f + 10f, p2_top.x + 8f, p2_top.y + 10f)
        lineTo(p2_bot.x + 8f, p2_bot.y + 10f)
        cubicTo(canvasW * 0.44f + 8f, canvasH * 0.69f + 10f, canvasW * 0.32f + 8f, canvasH * 0.66f + 10f, p1_bot.x + 8f, p1_bot.y + 10f)
        close()
    }
    drawPath(shadow2, color = ComicInkBlack.copy(alpha = 0.14f))

    val seg2Path = Path().apply {
        moveTo(p1_top.x, p1_top.y)
        cubicTo(canvasW * 0.50f + wave2, canvasH * 0.575f, canvasW * 0.62f + wave2, canvasH * 0.595f, p2_top.x, p2_top.y)
        cubicTo(
            p2_top.x + (p2_bot.x - p2_top.x) * 0.25f, p2_top.y + 12f,
            p2_top.x + (p2_bot.x - p2_top.x) * 0.65f, p2_bot.y - 8f,
            p2_bot.x, p2_bot.y
        )
        cubicTo(canvasW * 0.44f + wave2, canvasH * 0.695f, canvasW * 0.32f + wave1, canvasH * 0.660f, p1_bot.x, p1_bot.y)
        cubicTo(
            p1_bot.x + (p1_top.x - p1_bot.x) * 0.35f, p1_bot.y - 8f,
            p1_bot.x + (p1_top.x - p1_bot.x) * 0.75f, p1_top.y + 10f,
            p1_top.x, p1_top.y
        )
        close()
    }
    drawPath(seg2Path, color = paperColor)
    drawPath(seg2Path, color = ComicInkBlack, style = Stroke(width = strokeWidth, join = StrokeJoin.Round, cap = StrokeCap.Round))

    // Đường nét đứt & họa tiết đoạn 2
    val seg2Dash = ((scrollOffset + 60f) % 130f) / 130f
    for (k in 0..1) {
        val t = (seg2Dash + k * 0.50f) % 1.0f
        if (t in 0.10f..0.90f) {
            val lx = p1_bot.x + (p2_bot.x - p1_bot.x) * t
            val ly = p1_bot.y + (p2_bot.y - p1_bot.y) * t
            val rx = p1_top.x + (p2_top.x - p1_top.x) * t
            val ry = p1_top.y + (p2_top.y - p1_top.y) * t
            drawLine(
                color = inkColor.copy(alpha = 0.55f),
                start = Offset(lx, ly),
                end = Offset(rx, ry),
                strokeWidth = 3f,
                pathEffect = dashEffect
            )

            if (skin != SkinType.SCHOOL_CANTEEN) {
                drawIntoCanvas { canvas ->
                    val paint = AndroidPaint().apply {
                        textSize = 38f
                        textAlign = AndroidPaint.Align.CENTER
                        isAntiAlias = true
                    }
                    val mx = (lx + rx) / 2f
                    val my = (ly + ry) / 2f
                    canvas.nativeCanvas.drawText(skin.patternEmoji, mx, my + 12f, paint)
                }
            }
        }
    }

    // ── 3. ĐOẠN 1 (LỚP ĐỈNH: TỪ CUỘN GIẤY TRÊN GIÁ ĐỔ XUỐNG BÊN TRÁI) ──
    val leftLoop = Path().apply {
        moveTo(p1_top.x, p1_top.y)
        cubicTo(
            p1_bot.x - 26f, p1_top.y + 10f,
            p1_bot.x - 26f, p1_bot.y - 6f,
            p1_bot.x, p1_bot.y
        )
        cubicTo(
            p1_bot.x + (p1_top.x - p1_bot.x) * 0.35f, p1_bot.y - 8f,
            p1_bot.x + (p1_top.x - p1_bot.x) * 0.75f, p1_top.y + 10f,
            p1_top.x, p1_top.y
        )
        close()
    }
    drawPath(leftLoop, color = foldColor)
    drawPath(leftLoop, color = ComicInkBlack, style = Stroke(width = strokeWidth, join = StrokeJoin.Round))

    val shadow1 = Path().apply {
        moveTo(p0_L.x + 8f, p0_L.y + 10f)
        cubicTo(canvasW * 0.30f + 8f, canvasH * 0.48f + 10f, canvasW * 0.18f + 8f, canvasH * 0.54f + 10f, p1_bot.x + 8f, p1_bot.y + 10f)
        lineTo(p1_top.x + 8f, p1_top.y + 10f)
        cubicTo(canvasW * 0.46f + 8f, canvasH * 0.49f + 10f, canvasW * 0.52f + 8f, canvasH * 0.46f + 10f, p0_R.x + 8f, p0_R.y + 10f)
        close()
    }
    drawPath(shadow1, color = ComicInkBlack.copy(alpha = 0.14f))

    val seg1Path = Path().apply {
        moveTo(p0_L.x, p0_L.y)
        cubicTo(canvasW * 0.30f, canvasH * 0.485f, canvasW * 0.18f + wave1, canvasH * 0.545f, p1_bot.x, p1_bot.y)
        cubicTo(
            p1_bot.x + (p1_top.x - p1_bot.x) * 0.35f, p1_bot.y - 8f,
            p1_bot.x + (p1_top.x - p1_bot.x) * 0.75f, p1_top.y + 10f,
            p1_top.x, p1_top.y
        )
        cubicTo(canvasW * 0.46f + wave1, canvasH * 0.495f, canvasW * 0.52f, canvasH * 0.460f, p0_R.x, p0_R.y)
        close()
    }
    drawPath(seg1Path, color = paperColor)
    drawPath(seg1Path, color = ComicInkBlack, style = Stroke(width = strokeWidth, join = StrokeJoin.Round, cap = StrokeCap.Round))

    // Đường nét đứt & họa tiết đoạn 1
    val seg1Dash = ((scrollOffset + 120f) % 120f) / 120f
    for (k in 0..1) {
        val t = (seg1Dash + k * 0.50f) % 1.0f
        if (t in 0.10f..0.90f) {
            val lx = p0_L.x + (p1_bot.x - p0_L.x) * t
            val ly = p0_L.y + (p1_bot.y - p0_L.y) * t
            val rx = p0_R.x + (p1_top.x - p0_R.x) * t
            val ry = p0_R.y + (p1_top.y - p0_R.y) * t
            drawLine(
                color = inkColor.copy(alpha = 0.55f),
                start = Offset(lx, ly),
                end = Offset(rx, ry),
                strokeWidth = 3f,
                pathEffect = dashEffect
            )

            if (skin != SkinType.SCHOOL_CANTEEN) {
                drawIntoCanvas { canvas ->
                    val paint = AndroidPaint().apply {
                        textSize = 38f
                        textAlign = AndroidPaint.Align.CENTER
                        isAntiAlias = true
                    }
                    val mx = (lx + rx) / 2f
                    val my = (ly + ry) / 2f
                    canvas.nativeCanvas.drawText(skin.patternEmoji, mx, my + 12f, paint)
                }
            }
        }
    }
}

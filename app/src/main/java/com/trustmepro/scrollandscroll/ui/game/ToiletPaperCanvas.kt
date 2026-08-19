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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
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
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// ─────────────────────────────────────────────────────────────────────────────
// Cấu trúc điểm và đường cong Bezier bậc 3 với vector pháp tuyến
// ─────────────────────────────────────────────────────────────────────────────

private data class BezierPoint(
    val pos: Offset,
    val normal: Offset,
    val tangentAngleDeg: Float,
    val t: Float
)

/**
 * Tính toán tập hợp các điểm trên đường cong Cubic Bezier kèm vector pháp tuyến chính xác
 */
private fun sampleCubicBezier(
    p0: Offset, p1: Offset, p2: Offset, p3: Offset,
    samples: Int
): List<BezierPoint> {
    val points = ArrayList<BezierPoint>(samples + 1)
    for (i in 0..samples) {
        val t = i.toFloat() / samples
        val u = 1f - t
        val uu = u * u
        val uuu = uu * u
        val tt = t * t
        val ttt = tt * t

        // Tọa độ điểm trên đường cong
        val x = uuu * p0.x + 3f * uu * t * p1.x + 3f * u * tt * p2.x + ttt * p3.x
        val y = uuu * p0.y + 3f * uu * t * p1.y + 3f * u * tt * p2.y + ttt * p3.y
        val pos = Offset(x, y)

        // Vector tiếp tuyến (tangent)
        val tx = 3f * (uu * (p1.x - p0.x) + 2f * u * t * (p2.x - p1.x) + tt * (p3.x - p2.x))
        val ty = 3f * (uu * (p1.y - p0.y) + 2f * u * t * (p2.y - p1.y) + tt * (p3.y - p2.y))
        val tLen = sqrt(tx * tx + ty * ty).coerceAtLeast(0.0001f)
        val unitTx = tx / tLen
        val unitTy = ty / tLen

        // Vector pháp tuyến vuông góc hướng sang phải (normal)
        val normal = Offset(-unitTy, unitTx)
        val angleDeg = (atan2(ty.toDouble(), tx.toDouble()) * 180.0 / PI).toFloat()

        points.add(BezierPoint(pos, normal, angleDeg, t))
    }
    return points
}

// ─────────────────────────────────────────────────────────────────────────────
// ToiletPaperCanvas — Màn hình cuộn giấy 3D Isometric & Dải giấy uốn lượn sống động
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ToiletPaperCanvas(
    skin: SkinType,
    isOverdrive: Boolean,
    onScroll: (pixels: Float) -> Unit,
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
            animation = tween(durationMillis = 1400, easing = LinearEasing),
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

                        val deltaY = dragAmount.y
                        if (deltaY > 0f) {
                            rollRotationOffset += deltaY * 0.5f
                            paperScrollOffset += deltaY
                            currentVelocity = deltaY * 25f
                            onScroll(deltaY)
                        }
                    },
                    onDragEnd = {
                        val velocity = velocityTracker.calculateVelocity().y
                        currentVelocity = velocity
                        if (velocity > 250f) {
                            coroutineScope.launch {
                                var lastValue = 0f
                                flingAnim.snapTo(0f)
                                flingAnim.animateTo(
                                    targetValue = (velocity * 0.7f).coerceAtMost(3500f),
                                    animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
                                ) {
                                    val delta = this.value - lastValue
                                    if (delta > 0f) {
                                        rollRotationOffset += delta * 0.5f
                                        paperScrollOffset += delta
                                        currentVelocity = (targetValue - this.value) * 1.2f
                                        onScroll(delta)
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
        // 1. Hình nền nhà vệ sinh hoạt hình 2D Comic sạch sẽ
        Image(
            painter = painterResource(id = R.drawable.bg_bathroom_game),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Lớp Canvas tương tác vẽ Cuộn giấy 3D + Dải giấy S-Curve uốn lượn
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height

            // ── TỌA ĐỘ VÀ KÍCH THƯỚC TRỤC CUỘN GIẤY 3D ISOMETRIC ──
            // Căn khớp hoàn hảo với giá đỡ gỗ trên tường ở nền hoạt hình
            val rollLeftCenterX = canvasW * 0.44f
            val rollLeftCenterY = canvasH * 0.362f
            val rollCapRx = canvasW * 0.115f
            val rollCapRy = canvasW * 0.205f

            val rollRightCenterX = canvasW * 0.735f
            val rollRightCenterY = canvasH * 0.342f
            val rollRightCapRx = canvasW * 0.095f
            val rollRightCapRy = canvasW * 0.175f

            // A. Vẽ dải giấy S-Curve uốn lượn rơi xuống sàn
            drawRealisticSCurveRibbon(
                canvasW = canvasW,
                canvasH = canvasH,
                startOffset = Offset(rollLeftCenterX - rollCapRx * 0.2f, rollLeftCenterY + rollCapRy * 0.95f),
                scrollOffset = paperScrollOffset,
                flutterPhase = flutterPhase,
                velocity = currentVelocity,
                skin = skin,
                isOverdrive = isOverdrive
            )

            // B. Vẽ Cuộn Giấy Vệ Sinh 3D Đa Chiều (Isometric 3D Roll Cylinder)
            draw3DIsometricPaperRoll(
                leftCenter = Offset(rollLeftCenterX, rollLeftCenterY),
                leftRx = rollCapRx,
                leftRy = rollCapRy,
                rightCenter = Offset(rollRightCenterX, rollRightCenterY),
                rightRx = rollRightCapRx,
                rightRy = rollRightCapRy,
                rotationOffset = rollRotationOffset,
                skin = skin,
                velocity = currentVelocity
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Vẽ cuộn giấy 3D Isometric với mặt đáy Elip có lõi carton, lớp xoắn và thân ống
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.draw3DIsometricPaperRoll(
    leftCenter: Offset,
    leftRx: Float,
    leftRy: Float,
    rightCenter: Offset,
    rightRx: Float,
    rightRy: Float,
    rotationOffset: Float,
    skin: SkinType,
    velocity: Float
) {
    // 1. Bóng đổ êm dưới cuộn giấy lên tường
    val shadowPath = Path().apply {
        moveTo(leftCenter.x - leftRx + 8f, leftCenter.y + 12f)
        lineTo(rightCenter.x + rightRx + 12f, rightCenter.y + 16f)
        lineTo(rightCenter.x + rightRx + 12f, rightCenter.y + rightRy + 22f)
        lineTo(leftCenter.x - leftRx + 8f, leftCenter.y + leftRy + 20f)
        close()
    }
    drawPath(shadowPath, color = ComicInkBlack.copy(alpha = 0.22f))

    // 2. Thân cuộn giấy (3D Curved Cylinder Body)
    val bodyPath = Path().apply {
        moveTo(leftCenter.x, leftCenter.y - leftRy)
        lineTo(rightCenter.x, rightCenter.y - rightRy)
        // Cung elip bên phải
        cubicTo(
            rightCenter.x + rightRx * 1.33f, rightCenter.y - rightRy * 0.5f,
            rightCenter.x + rightRx * 1.33f, rightCenter.y + rightRy * 0.5f,
            rightCenter.x, rightCenter.y + rightRy
        )
        lineTo(leftCenter.x, leftCenter.y + leftRy)
        // Cung elip bên trái (nửa dưới)
        cubicTo(
            leftCenter.x - leftRx * 1.33f, leftCenter.y + leftRy * 0.5f,
            leftCenter.x - leftRx * 1.33f, leftCenter.y - leftRy * 0.5f,
            leftCenter.x, leftCenter.y - leftRy
        )
        close()
    }

    // Gradient màu thân giấy (Highlight sáng ở đỉnh, bóng mờ ở đáy)
    val bodyBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.98f),
            skin.primaryColor,
            skin.primaryColor,
            skin.accentColor.copy(alpha = 0.45f),
            skin.primaryColor.copy(alpha = 0.85f)
        ),
        startY = leftCenter.y - leftRy,
        endY = leftCenter.y + leftRy
    )
    drawPath(bodyPath, brush = bodyBrush)

    // 3. Đường nét đứt chia tờ giấy trên thân cuộn (Perforated lines xoay tròn)
    val seamSpacing = 52f
    val normRot = (rotationOffset % seamSpacing)
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(9f, 7f), 0f)

    for (k in -1..6) {
        val frac = ((k * seamSpacing + normRot) / (rightCenter.x - leftCenter.x + leftRx)).coerceIn(0f, 1f)
        if (frac in 0.05f..0.92f) {
            val topX = leftCenter.x + (rightCenter.x - leftCenter.x) * frac
            val topY = (leftCenter.y - leftRy) + ((rightCenter.y - rightRy) - (leftCenter.y - leftRy)) * frac
            val botX = leftCenter.x + (rightCenter.x - leftCenter.x) * frac
            val botY = (leftCenter.y + leftRy) + ((rightCenter.y + rightRy) - (leftCenter.y + leftRy)) * frac

            // Đường nét đứt cong nhẹ theo mặt trụ tròn
            val linePath = Path().apply {
                moveTo(topX, topY)
                cubicTo(
                    topX - 8f, topY + (botY - topY) * 0.35f,
                    topX - 8f, topY + (botY - topY) * 0.65f,
                    botX, botY
                )
            }
            drawPath(
                path = linePath,
                color = ComicInkBlack.copy(alpha = 0.45f),
                style = Stroke(width = 2.5f, pathEffect = dashEffect)
            )
        }
    }

    // 4. Viền mực đen phong cách Comic cho thân cuộn
    val bodyOutlineTop = Path().apply {
        moveTo(leftCenter.x, leftCenter.y - leftRy)
        lineTo(rightCenter.x, rightCenter.y - rightRy)
    }
    val bodyOutlineBot = Path().apply {
        moveTo(leftCenter.x, leftCenter.y + leftRy)
        lineTo(rightCenter.x, rightCenter.y + rightRy)
    }
    drawPath(bodyOutlineTop, color = ComicInkBlack, style = Stroke(width = 4.5f, cap = StrokeCap.Round))
    drawPath(bodyOutlineBot, color = ComicInkBlack, style = Stroke(width = 4.5f, cap = StrokeCap.Round))

    // 5. Mặt bên trái cuộn giấy (Left Ellipse Cap Face - Phối cảnh nhìn nghiêng)
    // A. Nền giấy mặt bên
    val leftCapPath = Path().apply {
        moveTo(leftCenter.x, leftCenter.y - leftRy)
        cubicTo(
            leftCenter.x - leftRx * 1.333f, leftCenter.y - leftRy,
            leftCenter.x - leftRx * 1.333f, leftCenter.y + leftRy,
            leftCenter.x, leftCenter.y + leftRy
        )
        cubicTo(
            leftCenter.x + leftRx * 1.333f, leftCenter.y + leftRy,
            leftCenter.x + leftRx * 1.333f, leftCenter.y - leftRy,
            leftCenter.x, leftCenter.y - leftRy
        )
        close()
    }
    drawPath(
        path = leftCapPath,
        brush = Brush.radialGradient(
            colors = listOf(
                skin.primaryColor.copy(alpha = 0.95f),
                skin.accentColor.copy(alpha = 0.35f),
                skin.primaryColor
            ),
            center = leftCenter,
            radius = leftRy
        )
    )

    // B. Các vòng xoắn đồng tâm thể hiện các lớp giấy quấn (Spirals)
    val numRings = 4
    for (r in 1..numRings) {
        val ringFraction = 0.35f + (r.toFloat() / (numRings + 1)) * 0.58f
        val rx = leftRx * ringFraction
        val ry = leftRy * ringFraction
        val ringPath = Path().apply {
            moveTo(leftCenter.x, leftCenter.y - ry)
            cubicTo(
                leftCenter.x - rx * 1.333f, leftCenter.y - ry,
                leftCenter.x - rx * 1.333f, leftCenter.y + ry,
                leftCenter.x, leftCenter.y + ry
            )
            cubicTo(
                leftCenter.x + rx * 1.333f, leftCenter.y + ry,
                leftCenter.x + rx * 1.333f, leftCenter.y - ry,
                leftCenter.x, leftCenter.y - ry
            )
            close()
        }
        drawPath(
            path = ringPath,
            color = ComicInkBlack.copy(alpha = 0.28f),
            style = Stroke(width = 1.8f)
        )
    }

    // C. Lõi Carton Carton Core (Ống bìa carton màu nâu ở tâm)
    val coreRx = leftRx * 0.30f
    val coreRy = leftRy * 0.30f
    val corePath = Path().apply {
        moveTo(leftCenter.x, leftCenter.y - coreRy)
        cubicTo(
            leftCenter.x - coreRx * 1.333f, leftCenter.y - coreRy,
            leftCenter.x - coreRx * 1.333f, leftCenter.y + coreRy,
            leftCenter.x, leftCenter.y + coreRy
        )
        cubicTo(
            leftCenter.x + coreRx * 1.333f, leftCenter.y + coreRy,
            leftCenter.x + coreRx * 1.333f, leftCenter.y - coreRy,
            leftCenter.x, leftCenter.y - coreRy
        )
        close()
    }
    // Màu ống carton
    drawPath(
        path = corePath,
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFD7CCC8), Color(0xFF8D6E63), Color(0xFF4E342E)),
            center = leftCenter,
            radius = coreRy
        )
    )
    drawPath(path = corePath, color = ComicInkBlack, style = Stroke(width = 3.5f))

    // D. Lỗ rỗng đen sâu ở giữa ống carton
    val holeRx = coreRx * 0.55f
    val holeRy = coreRy * 0.55f
    val holePath = Path().apply {
        moveTo(leftCenter.x, leftCenter.y - holeRy)
        cubicTo(
            leftCenter.x - holeRx * 1.333f, leftCenter.y - holeRy,
            leftCenter.x - holeRx * 1.333f, leftCenter.y + holeRy,
            leftCenter.x, leftCenter.y + holeRy
        )
        cubicTo(
            leftCenter.x + holeRx * 1.333f, leftCenter.y + holeRy,
            leftCenter.x + holeRx * 1.333f, leftCenter.y - holeRy,
            leftCenter.x, leftCenter.y - holeRy
        )
        close()
    }
    drawPath(path = holePath, color = ComicInkBlack)

    // E. Viền đen đậm Comic bao quanh toàn bộ mặt tròn bên trái
    drawPath(path = leftCapPath, color = ComicInkBlack, style = Stroke(width = 4.5f))

    // 6. Ánh sáng phản chiếu Specular Highlight trên đỉnh cuộn giấy
    val specPath = Path().apply {
        moveTo(leftCenter.x + 8f, leftCenter.y - leftRy + 4f)
        lineTo(rightCenter.x - 8f, rightCenter.y - rightRy + 4f)
        lineTo(rightCenter.x - 8f, rightCenter.y - rightRy + 14f)
        lineTo(leftCenter.x + 8f, leftCenter.y - leftRy + 14f)
        close()
    }
    drawPath(specPath, color = Color.White.copy(alpha = 0.65f))

    // 7. Hiệu ứng Comic Action: Các vệt tia nước/giọt nước sinh động bên góc trái
    drawActionDropsAndLines(leftCenter = leftCenter, leftRx = leftRx, leftRy = leftRy, velocity = velocity)
}

// ─────────────────────────────────────────────────────────────────────────────
// Vẽ hiệu ứng hoạt hình sống động: Giọt vung & đường gió cuộn quanh cuộn giấy
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawActionDropsAndLines(
    leftCenter: Offset,
    leftRx: Float,
    leftRy: Float,
    velocity: Float
) {
    // 3 giọt cong hoạt hình trên góc trái trên `( ( (`
    val dropColor = Color(0xFF64B5F6)
    val dropOutline = ComicInkBlack

    val dropPositions = listOf(
        Offset(leftCenter.x - leftRx * 0.75f, leftCenter.y - leftRy * 1.15f) to 14f,
        Offset(leftCenter.x - leftRx * 0.40f, leftCenter.y - leftRy * 1.38f) to 18f,
        Offset(leftCenter.x + leftRx * 0.05f, leftCenter.y - leftRy * 1.48f) to 12f
    )

    for ((pos, sz) in dropPositions) {
        val dropPath = Path().apply {
            moveTo(pos.x, pos.y - sz * 0.6f)
            cubicTo(pos.x + sz * 0.6f, pos.y - sz * 0.2f, pos.x + sz * 0.5f, pos.y + sz * 0.6f, pos.x, pos.y + sz * 0.6f)
            cubicTo(pos.x - sz * 0.5f, pos.y + sz * 0.6f, pos.x - sz * 0.6f, pos.y - sz * 0.2f, pos.x, pos.y - sz * 0.6f)
            close()
        }
        drawPath(dropPath, color = dropColor)
        drawPath(dropPath, color = dropOutline, style = Stroke(width = 2.5f))
    }

    // 2 vệt tốc độ xoay `) )` ở bên trái cuộn giấy khi cuộn
    val arcPath1 = Path().apply {
        moveTo(leftCenter.x - leftRx * 1.25f, leftCenter.y - leftRy * 0.45f)
        cubicTo(
            leftCenter.x - leftRx * 1.45f, leftCenter.y,
            leftCenter.x - leftRx * 1.45f, leftCenter.y + leftRy * 0.35f,
            leftCenter.x - leftRx * 1.22f, leftCenter.y + leftRy * 0.65f
        )
    }
    val arcPath2 = Path().apply {
        moveTo(leftCenter.x - leftRx * 1.40f, leftCenter.y - leftRy * 0.25f)
        cubicTo(
            leftCenter.x - leftRx * 1.62f, leftCenter.y + leftRy * 0.1f,
            leftCenter.x - leftRx * 1.62f, leftCenter.y + leftRy * 0.35f,
            leftCenter.x - leftRx * 1.38f, leftCenter.y + leftRy * 0.55f
        )
    }

    drawPath(arcPath1, color = dropOutline, style = Stroke(width = 3f, cap = StrokeCap.Round))
    drawPath(arcPath2, color = dropOutline, style = Stroke(width = 2.5f, cap = StrokeCap.Round))
}

// ─────────────────────────────────────────────────────────────────────────────
// Vẽ Dải Giấy Uốn Lượn S-Curve mềm mại rơi xuống đống giấy trên sàn
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawRealisticSCurveRibbon(
    canvasW: Float,
    canvasH: Float,
    startOffset: Offset,
    scrollOffset: Float,
    flutterPhase: Float,
    velocity: Float,
    skin: SkinType,
    isOverdrive: Boolean
) {
    val ribbonWidth = canvasW * 0.35f
    val halfWidth = ribbonWidth / 2f

    // Biên độ vẫy giấy nhịp nhàng theo tốc độ vuốt
    val speedFactor = (velocity / 1200f).coerceIn(0f, 2.5f)
    val waveAmpX1 = sin(flutterPhase) * (8f + speedFactor * 14f)
    val waveAmpX2 = cos(flutterPhase + 1.2f) * (10f + speedFactor * 18f)

    // ── CÁC ĐIỂM ĐIỀU KHIỂN ĐƯỜNG CONG CHỮ S LIỀN MẠCH ──
    // Đỉnh dải giấy: nối từ cuộn giấy
    val p0 = startOffset

    // Vòng cung bên trái (uốn lượn qua mép cửa sổ)
    val p1 = Offset(canvasW * 0.12f + waveAmpX1, canvasH * 0.49f)

    // Điểm uốn giữa (giao thoa chữ S)
    val p2 = Offset(canvasW * 0.76f + waveAmpX2, canvasH * 0.60f)

    // Đáy dải giấy: tiếp đất vào đống giấy trắng ở sàn nhà
    val p3 = Offset(canvasW * 0.44f, canvasH * 0.775f)

    // Lấy mẫu mịn đường cong Cubic Bezier
    val samples = 70
    val sampledPoints = sampleCubicBezier(p0, p1, p2, p3, samples)
    if (sampledPoints.size < 4) return

    val leftEdge = ArrayList<Offset>(sampledPoints.size)
    val rightEdge = ArrayList<Offset>(sampledPoints.size)

    for (bp in sampledPoints) {
        val nx = bp.normal.x
        val ny = bp.normal.y
        leftEdge.add(Offset(bp.pos.x - nx * halfWidth, bp.pos.y - ny * halfWidth))
        rightEdge.add(Offset(bp.pos.x + nx * halfWidth, bp.pos.y + ny * halfWidth))
    }

    // Đa giác toàn bộ dải giấy
    val ribbonPoly = Path().apply {
        moveTo(leftEdge[0].x, leftEdge[0].y)
        for (i in 1 until leftEdge.size) lineTo(leftEdge[i].x, leftEdge[i].y)
        for (i in rightEdge.indices.reversed()) lineTo(rightEdge[i].x, rightEdge[i].y)
        close()
    }

    // 1. Bóng đổ của dải giấy lên tường gạch xanh
    val shadowPoly = Path().apply {
        moveTo(leftEdge[0].x + 14f, leftEdge[0].y + 16f)
        for (i in 1 until leftEdge.size) lineTo(leftEdge[i].x + 14f, leftEdge[i].y + 16f)
        for (i in rightEdge.indices.reversed()) lineTo(rightEdge[i].x + 14f, rightEdge[i].y + 16f)
        close()
    }
    drawPath(shadowPoly, color = ComicInkBlack.copy(alpha = 0.20f))

    // 2. Nền dải giấy (Màu sắc theo Skin đang chọn + Gradient 3D)
    val ribbonGradient = if (isOverdrive) {
        Brush.verticalGradient(
            colors = listOf(
                skin.primaryColor,
                Color(0xFFFF7043),
                Color(0xFFFFD54F),
                skin.primaryColor
            ),
            startY = p0.y,
            endY = p3.y
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                skin.primaryColor,
                skin.primaryColor.copy(alpha = 0.96f),
                skin.accentColor.copy(alpha = 0.35f),
                skin.primaryColor
            ),
            startY = p0.y,
            endY = p3.y
        )
    }
    drawPath(ribbonPoly, brush = ribbonGradient)

    // 3. Vùng bóng đổ nội khối ở các nếp gấp chữ S (3D Fold Crease Shading)
    val innerFoldShade = Path().apply {
        val midStart = (samples * 0.38f).toInt()
        val midEnd = (samples * 0.65f).toInt()
        moveTo(leftEdge[midStart].x, leftEdge[midStart].y)
        for (k in midStart..midEnd) lineTo(leftEdge[k].x, leftEdge[k].y)
        for (k in midEnd downTo midStart) {
            val cx = (leftEdge[k].x + rightEdge[k].x) / 2f
            val cy = (leftEdge[k].y + rightEdge[k].y) / 2f
            lineTo(cx, cy)
        }
        close()
    }
    drawPath(innerFoldShade, color = ComicInkBlack.copy(alpha = 0.08f))

    // 4. Viền đen đậm nét truyện tranh Comic bao quanh dải giấy
    drawPath(
        path = ribbonPoly,
        color = ComicInkBlack,
        style = Stroke(width = 4.5f, join = StrokeJoin.Round, cap = StrokeCap.Round)
    )

    // 5. Các đường nét đứt cắt giấy (Perforated lines) & Họa tiết Skin trôi theo bước cuộn
    val sheetSpacingPoints = 14
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 9f), 0f)
    val scrollPhase = ((scrollOffset % 480f) / 480f * sheetSpacingPoints).toInt()

    var ptIdx = scrollPhase
    while (ptIdx < sampledPoints.size - 2) {
        val idx = ptIdx.coerceIn(0, sampledPoints.size - 1)
        val L = leftEdge[idx]
        val R = rightEdge[idx]

        // Đường đứt đoạn ngang vuông góc với dải giấy
        drawLine(
            color = ComicInkBlack.copy(alpha = 0.55f),
            start = Offset(L.x + (R.x - L.x) * 0.04f, L.y + (R.y - L.y) * 0.04f),
            end = Offset(L.x + (R.x - L.x) * 0.96f, L.y + (R.y - L.y) * 0.96f),
            strokeWidth = 3f,
            pathEffect = dashEffect
        )

        // Họa tiết Emoji của Skin in giữa từng tờ giấy
        val midIdx = (ptIdx + sheetSpacingPoints / 2).coerceIn(0, sampledPoints.size - 1)
        val midL = leftEdge[midIdx]
        val midR = rightEdge[midIdx]
        val emojiX = (midL.x + midR.x) / 2f
        val emojiY = (midL.y + midR.y) / 2f
        val bp = sampledPoints[midIdx]

        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.save()
            // Xoay emoji theo góc nghiêng tiếp tuyến của đường cong
            canvas.nativeCanvas.rotate(bp.tangentAngleDeg - 90f, emojiX, emojiY)
            val paint = AndroidPaint().apply {
                textSize = 42f
                textAlign = AndroidPaint.Align.CENTER
                isAntiAlias = true
            }
            canvas.nativeCanvas.drawText(skin.patternEmoji, emojiX, emojiY + 14f, paint)
            canvas.nativeCanvas.restore()
        }

        ptIdx += sheetSpacingPoints
    }
}

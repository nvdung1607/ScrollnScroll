package com.trustmepro.scrollandscroll.ui.game

import android.graphics.Paint as AndroidPaint
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
// Điểm mẫu trên đường cong Spline liên tục mượt mà (Catmull-Rom Spline)
// ─────────────────────────────────────────────────────────────────────────────

private data class SplineNode(
    val pos: Offset,
    val normal: Offset,
    val angleDeg: Float
)

/**
 * Lấy mẫu một dải Catmull-Rom Spline mượt mà liên tục C1 (không góc nhọn, không thắt nút)
 */
private fun sampleCatmullRomSpline(
    controlPoints: List<Offset>,
    samplesPerSegment: Int = 20
): List<SplineNode> {
    if (controlPoints.size < 4) return emptyList()

    val nodes = ArrayList<SplineNode>()

    for (i in 0 until controlPoints.size - 3) {
        val p0 = controlPoints[i]
        val p1 = controlPoints[i + 1]
        val p2 = controlPoints[i + 2]
        val p3 = controlPoints[i + 3]

        for (step in 0..samplesPerSegment) {
            if (i > 0 && step == 0) continue // tránh trùng điểm nối

            val t = step.toFloat() / samplesPerSegment
            val t2 = t * t
            val t3 = t2 * t

            // Tọa độ điểm theo công thức Catmull-Rom
            val x = 0.5f * (
                    (2f * p1.x) +
                    (-p0.x + p2.x) * t +
                    (2f * p0.x - 5f * p1.x + 4f * p2.x - p3.x) * t2 +
                    (-p0.x + 3f * p1.x - 3f * p2.x + p3.x) * t3
            )
            val y = 0.5f * (
                    (2f * p1.y) +
                    (-p0.y + p2.y) * t +
                    (2f * p0.y - 5f * p1.y + 4f * p2.y - p3.y) * t2 +
                    (-p0.y + 3f * p1.y - 3f * p2.y + p3.y) * t3
            )
            val pos = Offset(x, y)

            // Đạo hàm bậc 1 (tiếp tuyến tangent)
            val tx = 0.5f * (
                    (-p0.x + p2.x) +
                    2f * (2f * p0.x - 5f * p1.x + 4f * p2.x - p3.x) * t +
                    3f * (-p0.x + 3f * p1.x - 3f * p2.x + p3.x) * t2
            )
            val ty = 0.5f * (
                    (-p0.y + p2.y) +
                    2f * (2f * p0.y - 5f * p1.y + 4f * p2.y - p3.y) * t +
                    3f * (-p0.y + 3f * p1.y - 3f * p2.y + p3.y) * t2
            )

            val tLen = sqrt(tx * tx + ty * ty).coerceAtLeast(0.0001f)
            val unitTx = tx / tLen
            val unitTy = ty / tLen

            // Vector pháp tuyến vuông góc hướng sang phải
            val normal = Offset(-unitTy, unitTx)
            val angleDeg = (atan2(ty.toDouble(), tx.toDouble()) * 180.0 / PI).toFloat()

            nodes.add(SplineNode(pos, normal, angleDeg))
        }
    }
    return nodes
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
            animation = tween(durationMillis = 1600, easing = LinearEasing),
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
                            rollRotationOffset += deltaY * 0.55f
                            paperScrollOffset += deltaY
                            currentVelocity = deltaY * 30f
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
                                    targetValue = (velocity * 0.75f).coerceAtMost(3800f),
                                    animationSpec = tween(durationMillis = 950, easing = FastOutSlowInEasing)
                                ) {
                                    val delta = this.value - lastValue
                                    if (delta > 0f) {
                                        rollRotationOffset += delta * 0.55f
                                        paperScrollOffset += delta
                                        currentVelocity = (targetValue - this.value) * 1.3f
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

            // ── TỌA ĐỘ TRỤC CUỘN GIẤY 3D KHỚP CHÍNH XÁC VỚI KHUNG GỖ TRÊN NỀN ──
            // Mặt elip tròn bên phải: khớp ngay khớp nối trục tay gỗ
            val rightCapCenter = Offset(canvasW * 0.625f, canvasH * 0.380f)
            val rightCapRx = canvasW * 0.115f
            val rightCapRy = canvasW * 0.126f

            // Đầu bên trái của cuộn giấy: kéo dài theo thanh đỡ gỗ
            val leftEndCenter = Offset(canvasW * 0.385f, canvasH * 0.392f)
            val leftCapRx = canvasW * 0.088f
            val leftCapRy = canvasW * 0.118f

            // A. Vẽ dải giấy S-Curve uốn lượn dài, mềm mại, không góc nhọn, đổ vào sàn nhà
            drawSmoothFlowingPaperRibbon(
                canvasW = canvasW,
                canvasH = canvasH,
                startPoint = Offset(leftEndCenter.x + leftCapRx * 0.35f, leftEndCenter.y + leftCapRy * 0.70f),
                scrollOffset = paperScrollOffset,
                flutterPhase = flutterPhase,
                velocity = currentVelocity,
                skin = skin,
                isOverdrive = isOverdrive
            )

            // B. Vẽ Cuộn Giấy Vệ Sinh 3D Đa Chiều khớp 100% với khung gỗ
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
// Vẽ Cuộn Giấy 3D Khớp Hoàn Hảo Với Khung Gỗ
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
    // 1. Bóng đổ mềm dưới cuộn giấy lên tường gạch
    val shadowPath = Path().apply {
        moveTo(leftCenter.x - leftRx + 4f, leftCenter.y + 6f)
        lineTo(rightCenter.x + rightRx + 12f, rightCenter.y + 10f)
        lineTo(rightCenter.x + rightRx + 12f, rightCenter.y + rightRy + 18f)
        lineTo(leftCenter.x - leftRx + 4f, leftCenter.y + leftRy + 16f)
        close()
    }
    drawPath(shadowPath, color = ComicInkBlack.copy(alpha = 0.20f))

    // 2. Thân cuộn giấy hình trụ 3D (Cylinder Body)
    val bodyPath = Path().apply {
        // Cạnh trên từ trái sang phải
        moveTo(leftCenter.x, leftCenter.y - leftRy)
        lineTo(rightCenter.x, rightCenter.y - rightRy)
        // Cung elip bên phải (nửa trước)
        cubicTo(
            rightCenter.x + rightRx * 1.33f, rightCenter.y - rightRy * 0.5f,
            rightCenter.x + rightRx * 1.33f, rightCenter.y + rightRy * 0.5f,
            rightCenter.x, rightCenter.y + rightRy
        )
        // Cạnh dưới từ phải về trái
        lineTo(leftCenter.x, leftCenter.y + leftRy)
        // Cung elip bên trái
        cubicTo(
            leftCenter.x - leftRx * 1.33f, leftCenter.y + leftRy * 0.5f,
            leftCenter.x - leftRx * 1.33f, leftCenter.y - leftRy * 0.5f,
            leftCenter.x, leftCenter.y - leftRy
        )
        close()
    }

    // Gradient thân giấy
    val bodyBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.98f),
            skin.primaryColor,
            skin.primaryColor,
            skin.accentColor.copy(alpha = 0.38f),
            skin.primaryColor.copy(alpha = 0.85f)
        ),
        startY = leftCenter.y - leftRy,
        endY = leftCenter.y + leftRy
    )
    drawPath(bodyPath, brush = bodyBrush)

    // 3. Đường nét đứt chia tờ giấy trên thân cuộn xoay tròn
    val seamSpacing = 48f
    val normRot = (rotationOffset % seamSpacing)
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 7f), 0f)

    for (k in -1..6) {
        val frac = ((k * seamSpacing + normRot) / (rightCenter.x - leftCenter.x + rightRx)).coerceIn(0f, 1f)
        if (frac in 0.05f..0.94f) {
            val topX = leftCenter.x + (rightCenter.x - leftCenter.x) * frac
            val topY = (leftCenter.y - leftRy) + ((rightCenter.y - rightRy) - (leftCenter.y - leftRy)) * frac
            val botX = leftCenter.x + (rightCenter.x - leftCenter.x) * frac
            val botY = (leftCenter.y + leftRy) + ((rightCenter.y + rightRy) - (leftCenter.y + leftRy)) * frac

            val linePath = Path().apply {
                moveTo(topX, topY)
                cubicTo(
                    topX - 6f, topY + (botY - topY) * 0.35f,
                    topX - 6f, topY + (botY - topY) * 0.65f,
                    botX, botY
                )
            }
            drawPath(
                path = linePath,
                color = ComicInkBlack.copy(alpha = 0.38f),
                style = Stroke(width = 2.5f, pathEffect = dashEffect)
            )
        }
    }

    // 4. Viền mực đen Comic cho mép trên & mép dưới thân cuộn
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

    // 5. Đầu thanh trục gỗ nhô ra bên trái
    val woodenTipCenter = Offset(leftCenter.x - leftRx * 0.68f, leftCenter.y)
    val tipRadius = leftRy * 0.32f
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFD7CCC8), Color(0xFF8D6E63), Color(0xFF5D4037)),
            center = woodenTipCenter,
            radius = tipRadius
        ),
        radius = tipRadius,
        center = woodenTipCenter
    )
    drawCircle(
        color = ComicInkBlack,
        radius = tipRadius,
        center = woodenTipCenter,
        style = Stroke(width = 3.5f)
    )

    // 6. Mặt tròn bên phải cuộn giấy (Right Ellipse Cap)
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
                skin.primaryColor.copy(alpha = 0.98f),
                skin.accentColor.copy(alpha = 0.30f),
                skin.primaryColor
            ),
            center = rightCenter,
            radius = rightRy
        )
    )

    // A. Các vòng xoắn đồng tâm thể hiện nhiều lớp giấy quấn
    val numRings = 4
    for (r in 1..numRings) {
        val ringFraction = 0.32f + (r.toFloat() / (numRings + 1)) * 0.62f
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
            color = ComicInkBlack.copy(alpha = 0.28f),
            style = Stroke(width = 1.8f)
        )
    }

    // B. Lõi bìa carton ở tâm
    val coreRx = rightRx * 0.32f
    val coreRy = rightRy * 0.32f
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

    // C. Lỗ rỗng đen sâu ở giữa ống carton nơi trục gỗ luồn qua
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

    // 7. Vệt sáng phản chiếu Specular Highlight trên đỉnh cuộn giấy
    val specPath = Path().apply {
        moveTo(leftCenter.x + 8f, leftCenter.y - leftRy + 4f)
        lineTo(rightCenter.x - 8f, rightCenter.y - rightRy + 4f)
        lineTo(rightCenter.x - 8f, rightCenter.y - rightRy + 14f)
        lineTo(leftCenter.x + 8f, leftCenter.y - leftRy + 14f)
        close()
    }
    drawPath(specPath, color = Color.White.copy(alpha = 0.65f))

    // 8. 3 giọt nước bắn cong & 2 vệt gió xoay quanh cuộn giấy
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
// Vẽ Dải Giấy Uốn Lượn Mềm Mại, Dài, Không Góc Nhọn (Catmull-Rom Ribbon)
// ─────────────────────────────────────────────────────────────────────────────

private fun DrawScope.drawSmoothFlowingPaperRibbon(
    canvasW: Float,
    canvasH: Float,
    startPoint: Offset,
    scrollOffset: Float,
    flutterPhase: Float,
    velocity: Float,
    skin: SkinType,
    isOverdrive: Boolean
) {
    val ribbonWidth = canvasW * 0.32f
    val halfWidth = ribbonWidth / 2f

    // Biên độ vẫy giấy nhịp nhàng theo tốc độ vuốt
    val speedFactor = (velocity / 1200f).coerceIn(0f, 2.5f)
    val wave1 = sin(flutterPhase) * (5f + speedFactor * 10f)
    val wave2 = cos(flutterPhase + 1.2f) * (6f + speedFactor * 12f)
    val wave3 = sin(flutterPhase + 2.4f) * (4f + speedFactor * 8f)

    // ── DANH SÁCH ĐIỂM DẪN ĐƯỜNG CONG MỀM MẠI UỐN LƯỢN CHỮ S DÀI XUỐNG SÀN ──
    // Sử dụng Catmull-Rom Spline để nối mượt mà từ cuộn giấy -> vòng qua trái -> lượn qua phải -> đổ sâu vào sàn
    val guidePoints = listOf(
        // Điểm mở rộng ngoài biên để tính tiếp tuyến ban đầu
        Offset(startPoint.x + 30f, startPoint.y - 20f),

        // P0: Xuất phát từ cuộn giấy
        startPoint,

        // P1: Bắt đầu cong sang trái
        Offset(canvasW * 0.34f, canvasH * 0.50f),

        // P2: Vòng cung trái rộng, bo tròn hoàn hảo (ngang cửa sổ)
        Offset(canvasW * 0.22f + wave1, canvasH * 0.58f),

        // P3: Chuyển hướng cắt qua tâm
        Offset(canvasW * 0.33f, canvasH * 0.64f),

        // P4: Đang uốn sang phải
        Offset(canvasW * 0.54f + wave2, canvasH * 0.67f),

        // P5: Vòng cung phải rộng, bo tròn hoàn hảo
        Offset(canvasW * 0.70f + wave3, canvasH * 0.70f),

        // P6: Uốn xuống dưới về phía sàn
        Offset(canvasW * 0.56f, canvasH * 0.75f),

        // P7: Tiếp đất sâu vào giữa đống giấy ở sàn nhà (Kéo dài xuống 81% chiều cao)
        Offset(canvasW * 0.42f, canvasH * 0.81f),

        // Điểm mở rộng tiếp đất
        Offset(canvasW * 0.36f, canvasH * 0.86f)
    )

    // Lấy mẫu mịn đường cong Spline
    val sampledNodes = sampleCatmullRomSpline(guidePoints, samplesPerSegment = 18)
    if (sampledNodes.size < 6) return

    val leftEdge = ArrayList<Offset>(sampledNodes.size)
    val rightEdge = ArrayList<Offset>(sampledNodes.size)

    for (node in sampledNodes) {
        val nx = node.normal.x
        val ny = node.normal.y
        leftEdge.add(Offset(node.pos.x - nx * halfWidth, node.pos.y - ny * halfWidth))
        rightEdge.add(Offset(node.pos.x + nx * halfWidth, node.pos.y + ny * halfWidth))
    }

    // Đa giác dải giấy hoàn chỉnh
    val ribbonPoly = Path().apply {
        moveTo(leftEdge[0].x, leftEdge[0].y)
        for (i in 1 until leftEdge.size) lineTo(leftEdge[i].x, leftEdge[i].y)
        for (i in rightEdge.indices.reversed()) lineTo(rightEdge[i].x, rightEdge[i].y)
        close()
    }

    // 1. Bóng đổ dải giấy lên tường gạch xanh
    val shadowPoly = Path().apply {
        moveTo(leftEdge[0].x + 12f, leftEdge[0].y + 14f)
        for (i in 1 until leftEdge.size) lineTo(leftEdge[i].x + 12f, leftEdge[i].y + 14f)
        for (i in rightEdge.indices.reversed()) lineTo(rightEdge[i].x + 12f, rightEdge[i].y + 14f)
        close()
    }
    drawPath(shadowPoly, color = ComicInkBlack.copy(alpha = 0.20f))

    // 2. Nền dải giấy mềm mại
    val ribbonGradient = if (isOverdrive) {
        Brush.verticalGradient(
            colors = listOf(
                skin.primaryColor,
                Color(0xFFFF7043),
                Color(0xFFFFD54F),
                skin.primaryColor
            ),
            startY = startPoint.y,
            endY = canvasH * 0.81f
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                skin.primaryColor,
                skin.primaryColor.copy(alpha = 0.96f),
                skin.accentColor.copy(alpha = 0.32f),
                skin.primaryColor
            ),
            startY = startPoint.y,
            endY = canvasH * 0.81f
        )
    }
    drawPath(ribbonPoly, brush = ribbonGradient)

    // 3. Bóng nếp gấp nội khối (Crease Shading)
    val innerFoldShade = Path().apply {
        val midStart = (sampledNodes.size * 0.35f).toInt()
        val midEnd = (sampledNodes.size * 0.65f).toInt()
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

    // 4. Viền đen đậm Comic bao quanh toàn bộ dải giấy (bo tròn StrokeJoin.Round)
    drawPath(
        path = ribbonPoly,
        color = ComicInkBlack,
        style = Stroke(width = 4.5f, join = StrokeJoin.Round, cap = StrokeCap.Round)
    )

    // 5. Đường nét đứt xé giấy (Perforated lines) và Icon Emoji xoay theo tiếp tuyến
    val sheetSpacing = 16
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(11f, 8f), 0f)
    val scrollPhase = ((scrollOffset % 480f) / 480f * sheetSpacing).toInt()

    var ptIdx = scrollPhase
    while (ptIdx < sampledNodes.size - 2) {
        val idx = ptIdx.coerceIn(0, sampledNodes.size - 1)
        val L = leftEdge[idx]
        val R = rightEdge[idx]

        // Đường đứt đoạn xé giấy
        drawLine(
            color = ComicInkBlack.copy(alpha = 0.50f),
            start = Offset(L.x + (R.x - L.x) * 0.04f, L.y + (R.y - L.y) * 0.04f),
            end = Offset(L.x + (R.x - L.x) * 0.96f, L.y + (R.y - L.y) * 0.96f),
            strokeWidth = 3f,
            pathEffect = dashEffect
        )

        // Họa tiết Emoji in trên từng tờ giấy
        val midIdx = (ptIdx + sheetSpacing / 2).coerceIn(0, sampledNodes.size - 1)
        val midL = leftEdge[midIdx]
        val midR = rightEdge[midIdx]
        val emojiX = (midL.x + midR.x) / 2f
        val emojiY = (midL.y + midR.y) / 2f
        val node = sampledNodes[midIdx]

        drawIntoCanvas { canvas ->
            canvas.nativeCanvas.save()
            canvas.nativeCanvas.rotate(node.angleDeg - 90f, emojiX, emojiY)
            val paint = AndroidPaint().apply {
                textSize = 40f
                textAlign = AndroidPaint.Align.CENTER
                isAntiAlias = true
            }
            canvas.nativeCanvas.drawText(skin.patternEmoji, emojiX, emojiY + 14f, paint)
            canvas.nativeCanvas.restore()
        }

        ptIdx += sheetSpacing
    }
}

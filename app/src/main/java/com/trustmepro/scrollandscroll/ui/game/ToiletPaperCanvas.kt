package com.trustmepro.scrollandscroll.ui.game

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import com.trustmepro.scrollandscroll.data.model.SkinType
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * Canvas cuộn giấy vệ sinh tương tác với cử chỉ Drag 1:1 và Fling quán tính mượt mà
 */
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
    val flingAnim = remember { Animatable(0f) }

    val velocityTracker = remember { VelocityTracker() }

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
                            rollRotationOffset += deltaY * 0.4f
                            paperScrollOffset = (paperScrollOffset + deltaY) % 300f
                            onScroll(deltaY)
                        }
                    },
                    onDragEnd = {
                        val velocity = velocityTracker.calculateVelocity().y
                        if (velocity > 400f) {
                            coroutineScope.launch {
                                var lastValue = 0f
                                flingAnim.snapTo(0f)
                                flingAnim.animateTo(
                                    targetValue = (velocity * 0.6f).coerceAtMost(3000f),
                                    animationSpec = tween(durationMillis = 800)
                                ) {
                                    val delta = this.value - lastValue
                                    if (delta > 0f) {
                                        rollRotationOffset += delta * 0.4f
                                        paperScrollOffset = (paperScrollOffset + delta) % 300f
                                        onScroll(delta)
                                    }
                                    lastValue = this.value
                                }
                            }
                        }
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val rollWidth = canvasWidth * 0.52f
            val rollHeight = rollWidth * 0.58f
            val rollLeft = (canvasWidth - rollWidth) / 2f
            val rollTop = canvasHeight * 0.08f

            // 1. Draw Metallic Wall Holder
            drawHolder(rollLeft, rollTop, rollWidth, rollHeight)

            // 2. Draw Dangling Paper Sheet
            val paperWidth = rollWidth * 0.92f
            val paperLeft = (canvasWidth - paperWidth) / 2f
            val paperTop = rollTop + rollHeight * 0.45f
            val paperBottom = canvasHeight

            drawDanglingPaper(
                left = paperLeft,
                top = paperTop,
                width = paperWidth,
                bottom = paperBottom,
                scrollOffset = paperScrollOffset,
                skin = skin,
                isOverdrive = isOverdrive
            )

            // 3. Draw Cylinder Toilet Paper Roll
            drawPaperRoll(
                left = rollLeft,
                top = rollTop,
                width = rollWidth,
                height = rollHeight,
                rotationOffset = rollRotationOffset,
                skin = skin
            )
        }
    }
}

private fun DrawScope.drawHolder(
    rollLeft: Float,
    rollTop: Float,
    rollWidth: Float,
    rollHeight: Float
) {
    val barColor = Color(0xFF71717A)
    val highlightColor = Color(0xFFA1A1AA)

    // Holder Horizontal Bar
    val barHeight = 14f
    val barY = rollTop - 18f
    val barLeft = rollLeft - 24f
    val barRight = rollLeft + rollWidth + 24f

    drawRoundRect(
        color = barColor,
        topLeft = Offset(barLeft, barY),
        size = Size(barRight - barLeft, barHeight),
        cornerRadius = CornerRadius(6f, 6f)
    )
    drawRoundRect(
        color = highlightColor,
        topLeft = Offset(barLeft + 4f, barY + 2f),
        size = Size(barRight - barLeft - 8f, 4f),
        cornerRadius = CornerRadius(2f, 2f)
    )

    // Left Bracket
    drawRoundRect(
        color = barColor,
        topLeft = Offset(barLeft + 10f, barY - 20f),
        size = Size(16f, 32f),
        cornerRadius = CornerRadius(4f, 4f)
    )
    // Right Bracket
    drawRoundRect(
        color = barColor,
        topLeft = Offset(barRight - 26f, barY - 20f),
        size = Size(16f, 32f),
        cornerRadius = CornerRadius(4f, 4f)
    )
}

private fun DrawScope.drawPaperRoll(
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    rotationOffset: Float,
    skin: SkinType
) {
    // Outer Roll Cylinder Body
    val rollBrush = Brush.horizontalGradient(
        colors = listOf(
            skin.primaryColor.copy(alpha = 0.85f),
            skin.primaryColor,
            skin.accentColor.copy(alpha = 0.5f),
            skin.primaryColor
        ),
        startX = left,
        endX = left + width
    )

    drawRoundRect(
        brush = rollBrush,
        topLeft = Offset(left, top),
        size = Size(width, height),
        cornerRadius = CornerRadius(height * 0.45f, height * 0.45f)
    )

    // Roll Border Outline
    drawRoundRect(
        color = Color(0xFF27272A).copy(alpha = 0.35f),
        topLeft = Offset(left, top),
        size = Size(width, height),
        cornerRadius = CornerRadius(height * 0.45f, height * 0.45f),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.5f)
    )

    // Left Inner Core Oval (Cardboard tube)
    val coreWidth = width * 0.18f
    val coreHeight = height * 0.7f
    val coreCenter = Offset(left + coreWidth * 0.75f, top + height / 2f)

    drawOval(
        color = Color(0xFF8D6E63),
        topLeft = Offset(coreCenter.x - coreWidth / 2f, coreCenter.y - coreHeight / 2f),
        size = Size(coreWidth, coreHeight)
    )
    drawOval(
        color = Color(0xFF4E342E),
        topLeft = Offset(coreCenter.x - coreWidth * 0.35f, coreCenter.y - coreHeight * 0.35f),
        size = Size(coreWidth * 0.7f, coreHeight * 0.7f)
    )

    // Rotating Paper Seam Lines on Cylinder Body
    val seamSpacing = 50f
    val normalizedOffset = rotationOffset % seamSpacing
    for (i in 0..6) {
        val lineX = left + coreWidth + (i * seamSpacing + normalizedOffset) % (width - coreWidth - 10f)
        if (lineX < left + width - 10f) {
            drawLine(
                color = skin.accentColor.copy(alpha = 0.45f),
                start = Offset(lineX, top + 6f),
                end = Offset(lineX, top + height - 6f),
                strokeWidth = 2.5f
            )
        }
    }
}

private fun DrawScope.drawDanglingPaper(
    left: Float,
    top: Float,
    width: Float,
    bottom: Float,
    scrollOffset: Float,
    skin: SkinType,
    isOverdrive: Boolean
) {
    val height = bottom - top

    // Hanging Paper Sheet Body
    val sheetBrush = Brush.verticalGradient(
        colors = listOf(
            skin.primaryColor,
            skin.primaryColor.copy(alpha = 0.95f),
            skin.accentColor.copy(alpha = 0.25f)
        ),
        startY = top,
        endY = bottom
    )

    // Paper Sheet Path with wavy bottom edge
    val paperPath = Path().apply {
        moveTo(left, top)
        lineTo(left + width, top)
        lineTo(left + width, bottom - 20f)

        // Serrated / wavy cut bottom edge
        val segments = 8
        val segWidth = width / segments
        for (i in segments downTo 1) {
            val startX = left + i * segWidth
            val midX = startX - segWidth / 2f
            val endX = startX - segWidth
            quadraticTo(midX, bottom + 5f, endX, bottom - 15f)
        }
        close()
    }

    drawPath(path = paperPath, brush = sheetBrush)

    // Paper Outline
    drawPath(
        path = paperPath,
        color = Color(0xFF27272A).copy(alpha = 0.25f),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f)
    )

    // Perforated Dashed Tear-Lines every 260px
    val tearSpacing = 260f
    val firstTearY = top + (scrollOffset % tearSpacing)
    var currentTearY = firstTearY

    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
    while (currentTearY < bottom) {
        drawLine(
            color = skin.accentColor.copy(alpha = 0.7f),
            start = Offset(left + 8f, currentTearY),
            end = Offset(left + width - 8f, currentTearY),
            strokeWidth = 3f,
            pathEffect = dashEffect
        )

        // Draw Pattern Emojis on each paper sheet segment
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                textSize = 54f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }

            val emojiY = currentTearY - tearSpacing / 2f
            if (emojiY > top + 30f && emojiY < bottom - 30f) {
                canvas.nativeCanvas.drawText(
                    skin.patternEmoji,
                    left + width / 2f,
                    emojiY,
                    paint
                )
            }
        }

        currentTearY += tearSpacing
    }
}

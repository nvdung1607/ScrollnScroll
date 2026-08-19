package com.trustmepro.scrollandscroll.ui.game.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustmepro.scrollandscroll.ui.theme.OverdriveFire
import java.util.Locale

@Composable
fun SpsGauge(
    sps: Float,
    isOverdrive: Boolean,
    modifier: Modifier = Modifier
) {
    val maxSps = 10.0f
    val progressTarget = (sps / maxSps).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(durationMillis = 150),
        label = "spsProgress"
    )

    val gaugeColor by animateColorAsState(
        targetValue = when {
            isOverdrive -> OverdriveFire
            sps >= 5.0f -> MaterialTheme.colorScheme.secondary
            sps >= 2.0f -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.tertiary
        },
        label = "gaugeColor"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isOverdrive) Icons.Default.LocalFireDepartment else Icons.Default.Speed,
                    contentDescription = null,
                    tint = gaugeColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isOverdrive) "BURNING OVERDRIVE x1.5" else "TỐC ĐỘ VUỐT",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = gaugeColor,
                    letterSpacing = 0.5.sp
                )
            }

            Text(
                text = String.format(Locale.US, "%.1f vuốt/s", sps),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isOverdrive) OverdriveFire else MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = if (isOverdrive) {
                                listOf(MaterialTheme.colorScheme.primary, OverdriveFire)
                            } else {
                                listOf(gaugeColor.copy(alpha = 0.7f), gaugeColor)
                            }
                        )
                    )
            )
        }
    }
}

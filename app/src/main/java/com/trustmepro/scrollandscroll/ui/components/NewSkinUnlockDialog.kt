package com.trustmepro.scrollandscroll.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustmepro.scrollandscroll.data.model.SkinType
import com.trustmepro.scrollandscroll.ui.theme.ComicFontFamily

/**
 * Modal chúc mừng mở khóa Skin giấy mới kèm nút [DÙNG NGAY BÂY GIỜ]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSkinUnlockDialog(
    skin: SkinType,
    onEquipNow: () -> Unit,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFFFFDF0))
                .border(width = 4.dp, color = ComicInkBlack, shape = RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tiêu đề
                Text(
                    text = "🎉 MỞ KHÓA SKIN MỚI! 🎉",
                    fontFamily = ComicFontFamily,
                    fontSize = 20.sp,
                    color = ComicInkBlack
                )

                Spacer(Modifier.height(14.dp))

                // Preview Icon Tròn
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .shadow(6.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    skin.primaryColor.copy(alpha = 0.9f),
                                    skin.primaryColor,
                                    skin.accentColor
                                )
                            )
                        )
                        .border(3.dp, ComicInkBlack, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = skin.patternEmoji,
                        fontSize = 46.sp
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Tên Skin
                Text(
                    text = skin.displayName,
                    fontFamily = ComicFontFamily,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(4.dp))

                // Mô tả
                Text(
                    text = skin.description,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = ComicInkBlack.copy(alpha = 0.75f),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(Modifier.height(18.dp))

                // Nút [DÙNG NGAY BÂY GIỜ]
                M3PrimaryButton(
                    onClick = onEquipNow,
                    containerColor = ComicYellow,
                    contentColor = ComicInkBlack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🧻 DÙNG NGAY BÂY GIỜ",
                        fontFamily = ComicFontFamily,
                        fontSize = 15.sp
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Nút [ĐỂ SAU]
                M3TonalButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ĐỂ SAU",
                        fontFamily = ComicFontFamily,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

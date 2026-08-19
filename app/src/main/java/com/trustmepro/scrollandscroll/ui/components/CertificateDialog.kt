package com.trustmepro.scrollandscroll.ui.components

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustmepro.scrollandscroll.data.model.BadgeType
import com.trustmepro.scrollandscroll.ui.theme.ComicFontFamily
import com.trustmepro.scrollandscroll.util.ShareHelper
import java.util.Locale

/**
 * Modal Bằng Khen Danh Dự Vô Tri với nút Chia sẻ Story và lưu trữ
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificateDialog(
    badge: BadgeType,
    nickname: String,
    totalMeters: Double,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFFFFDF0)) // Màu giấy chứng nhận cổ điển
                .border(width = 4.dp, color = ComicInkBlack, shape = RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Quốc hiệu
                Text(
                    text = "VIỆN HÀN LÂM KHOA HỌC VÔ TRI",
                    fontFamily = ComicFontFamily,
                    fontSize = 14.sp,
                    color = ComicInkBlack,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "« Cuộn Bất Tận - Vô Tri Bất Diệt »",
                    style = MaterialTheme.typography.labelSmall,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFF5D4037)
                )

                Spacer(Modifier.height(8.dp))

                // Đường viền vàng phân cách
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(2.dp)
                        .background(ComicGold)
                )

                Spacer(Modifier.height(10.dp))

                // 2. Tiêu đề Bằng Khen
                Text(
                    text = "BẰNG KHEN DANH DỰ",
                    fontFamily = ComicFontFamily,
                    fontSize = 24.sp,
                    color = Color(0xFFFF5722)
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Trân trọng trao tặng cho Chiến Thần:",
                    style = MaterialTheme.typography.bodySmall,
                    color = ComicInkBlack.copy(alpha = 0.7f)
                )

                val displayName = if (nickname.isBlank()) "Chiến Thần Giấu Tên" else nickname
                Text(
                    text = "★ $displayName ★",
                    fontFamily = ComicFontFamily,
                    fontSize = 20.sp,
                    color = ComicInkBlack
                )

                Spacer(Modifier.height(10.dp))

                // 3. Emoji Huân Chương
                Text(
                    text = badge.badgeEmoji,
                    fontSize = 54.sp
                )

                Spacer(Modifier.height(6.dp))

                // 4. Tên Danh hiệu
                Text(
                    text = "[ ${badge.title} ]",
                    fontFamily = ComicFontFamily,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(4.dp))

                // 5. Lời cà khịa
                Text(
                    text = "\"${badge.description}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF37474F),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(Modifier.height(12.dp))

                // 6. Khung thống kê mét
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ComicInkBlack)
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "ĐÃ TIÊU TỐN TỔNG CỘNG",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ComicYellow
                        )
                        Text(
                            text = "${String.format(Locale.US, "%,.1f", totalMeters)} MÉT GIẤY",
                            fontFamily = ComicFontFamily,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // 7. Nút [📲 CHIA SẺ STORY]
                M3PrimaryButton(
                    onClick = {
                        ShareHelper.shareCertificate(context, badge, nickname, totalMeters)
                    },
                    containerColor = ComicOrange,
                    contentColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "📲 CHIA SẺ LÊN STORY",
                        fontFamily = ComicFontFamily,
                        fontSize = 15.sp
                    )
                }

                Spacer(Modifier.height(8.dp))

                // 8. Nút [TIẾP TỤC CUỘN]
                M3TonalButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "TIẾP TỤC CUỘN",
                        fontFamily = ComicFontFamily,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

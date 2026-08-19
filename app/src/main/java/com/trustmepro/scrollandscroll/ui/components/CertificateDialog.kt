package com.trustmepro.scrollandscroll.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustmepro.scrollandscroll.R
import com.trustmepro.scrollandscroll.data.model.BadgeType
import com.trustmepro.scrollandscroll.ui.theme.ComicFontFamily
import com.trustmepro.scrollandscroll.util.ShareHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Modal Bằng Khen Danh Dự chuẩn phôi Giấy Khen Việt Nam truyền thống
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
    val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Khung Bằng Khen Giấy Khen Chuẩn Mẫu ──────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.36f) // Tỷ lệ chuẩn phôi Giấy Khen Việt Nam
                    .shadow(16.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFFFDF5))
                    .border(2.5.dp, Color(0xFFD32F2F), RoundedCornerShape(12.dp))
            ) {
                // 1. Ảnh phôi giấy khen hoa văn viền đỏ vàng
                Image(
                    painter = painterResource(id = R.drawable.bg_certificate_template),
                    contentDescription = "Giấy Khen Template",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )

                // 2. Nội dung text điền trực tiếp vào các dòng phôi giấy khen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Khoảng trống đỉnh đầu cho Quốc Huy & Quốc Hiệu
                    Spacer(Modifier.height(34.dp))

                    // Dòng tiêu đề hiệu trưởng / Viện trưởng
                    Text(
                        text = "VIỆN TRƯỞNG VIỆN KHOA HỌC VÔ TRI",
                        fontFamily = ComicFontFamily,
                        fontSize = 11.sp,
                        color = Color(0xFF1E1B18),
                        letterSpacing = 0.5.sp
                    )

                    // Khen tặng em / Chiến Thần
                    val displayName = if (nickname.isBlank()) "Chiến Thần Giấu Tên" else nickname
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Khen tặng: ",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = Color(0xFF1E1B18)
                        )
                        Text(
                            text = displayName,
                            fontFamily = ComicFontFamily,
                            fontSize = 13.sp,
                            color = Color(0xFFD32F2F)
                        )
                        Text(
                            text = "  - Lớp: ",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = Color(0xFF1E1B18)
                        )
                        Text(
                            text = "Hội Vô Tri Toàn Cầu",
                            fontFamily = ComicFontFamily,
                            fontSize = 11.sp,
                            color = Color(0xFF1E1B18)
                        )
                    }

                    // Đạt danh hiệu
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Đạt danh hiệu: ",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = Color(0xFF1E1B18)
                        )
                        Text(
                            text = "${badge.title} ${badge.badgeEmoji}",
                            fontFamily = ComicFontFamily,
                            fontSize = 13.sp,
                            color = Color(0xFFD32F2F)
                        )
                    }

                    // Thành tích số mét cuộn
                    Text(
                        text = "Thành tích: Đã cuộn ${String.format(Locale.US, "%,.1f", totalMeters)} mét giấy vệ sinh vô tận",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp,
                        color = Color(0xFF2E7D32)
                    )

                    // Lời phê / Lời cà khịa
                    Text(
                        text = "\"${badge.description}\"",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        fontSize = 9.sp,
                        color = Color(0xFF5D4037),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Dòng ngày tháng và chữ ký / Dấu mộc đáy
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Góc dưới bên trái: Vào sổ khen thưởng
                        Column {
                            Text(
                                text = "Vào sổ khen thưởng: Số 3669/QĐ-VOTRI",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 7.5.sp,
                                color = Color(0xFF555555)
                            )
                            Text(
                                text = "Ngày $currentDate",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 7.5.sp,
                                color = Color(0xFF555555)
                            )
                        }

                        // Góc dưới bên phải: Chức vụ & Dấu mộc đỏ
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Việt Nam, ngày $currentDate",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 7.5.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = Color(0xFF333333)
                                )
                                Text(
                                    text = "Viện Trưởng",
                                    fontFamily = ComicFontFamily,
                                    fontSize = 9.sp,
                                    color = Color(0xFF1E1B18)
                                )
                                Spacer(Modifier.height(14.dp))
                                Text(
                                    text = "Scroll Master",
                                    fontFamily = ComicFontFamily,
                                    fontSize = 8.5.sp,
                                    color = Color(0xFFD32F2F)
                                )
                            }

                            // Con dấu mộc đỏ tròn 100% Vô Tri đè lên chữ ký
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .offset(x = 8.dp, y = 4.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, Color(0xFFD32F2F).copy(alpha = 0.85f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "CHỨNG NHẬN\n100% VÔ TRI",
                                    fontSize = 5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFD32F2F),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 6.sp
                                )
                            }
                        }
                    }
                }
            }

            // ── Các Nút Hành Động ───────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Nút Chia Sẻ Story
                M3PrimaryButton(
                    onClick = {
                        ShareHelper.shareCertificate(context, badge, nickname, totalMeters)
                    },
                    containerColor = Color(0xFFD32F2F),
                    contentColor = Color.White,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "📲 CHIA SẺ",
                        fontFamily = ComicFontFamily,
                        fontSize = 13.sp
                    )
                }

                // Nút Tải Về Thư Viện
                M3PrimaryButton(
                    onClick = {
                        ShareHelper.downloadCertificateToGallery(context, badge, nickname, totalMeters)
                    },
                    containerColor = ComicYellow,
                    contentColor = ComicInkBlack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "💾 TẢI VỀ ẢNH",
                        fontFamily = ComicFontFamily,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

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

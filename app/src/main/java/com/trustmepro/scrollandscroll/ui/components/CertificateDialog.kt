package com.trustmepro.scrollandscroll.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustmepro.scrollandscroll.data.model.BadgeType
import com.trustmepro.scrollandscroll.ui.theme.ComicFontFamily
import com.trustmepro.scrollandscroll.util.ShareHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Modal Bằng Khen Danh Dự thuần Vector & Canvas vẽ trực tiếp chuẩn mẫu Giấy Khen Việt Nam
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
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Khung Giấy Khen Vẽ Thuần Bằng Canvas & Vector (Không bị đè chữ) ────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.38f)
                    .shadow(16.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFFFDF2))
            ) {
                // 1. Lớp Canvas vẽ toàn bộ viền hoa văn đỏ-vàng, góc cổ điển và chìm hoa sen
                TraditionalCertificateBorder(
                    modifier = Modifier.fillMaxSize()
                )

                // 2. Lớp Nội dung Text trình bày chuẩn quy cách Giấy Khen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 22.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header: Quốc Hiệu & Tiêu Ngữ
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(
                            text = "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.5.sp,
                            color = Color(0xFF1A1A1A),
                            letterSpacing = 0.3.sp
                        )
                        Text(
                            text = "Độc lập – Tự do – Hạnh phúc",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            color = Color(0xFF1A1A1A)
                        )
                        Text(
                            text = "----------o0o----------",
                            fontFamily = FontFamily.Serif,
                            fontSize = 7.sp,
                            color = Color(0xFF555555),
                            modifier = Modifier.offset(y = (-2).dp)
                        )
                    }

                    // Tiêu đề lớn GIẤY KHEN
                    Text(
                        text = "GIẤY KHEN",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = Color(0xFFD32F2F),
                        letterSpacing = 2.sp
                    )

                    // Dòng thẩm quyền trao tặng
                    Text(
                        text = "VIỆN TRƯỞNG VIỆN KHOA HỌC VÔ TRI",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp,
                        color = Color(0xFF1A1A1A),
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
                            fontFamily = FontFamily.Serif,
                            fontSize = 9.5.sp,
                            color = Color(0xFF1A1A1A)
                        )
                        Text(
                            text = displayName,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = Color(0xFFD32F2F)
                        )
                        Text(
                            text = "   Lớp: ",
                            fontFamily = FontFamily.Serif,
                            fontSize = 9.5.sp,
                            color = Color(0xFF1A1A1A)
                        )
                        Text(
                            text = "Hội Vô Tri Toàn Cầu",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp,
                            color = Color(0xFF1A1A1A)
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
                            fontFamily = FontFamily.Serif,
                            fontSize = 9.5.sp,
                            color = Color(0xFF1A1A1A)
                        )
                        Text(
                            text = "${badge.title} ${badge.badgeEmoji}",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = Color(0xFFD32F2F)
                        )
                    }

                    // Thành tích số mét cuộn
                    Text(
                        text = "Thành tích: Đã cuộn ${String.format(Locale.US, "%,.1f", totalMeters)} mét giấy vệ sinh vô tận",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = Color(0xFF1B5E20)
                    )

                    // Lời phê / Lời cà khịa
                    Text(
                        text = "\"${badge.description}\"",
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 8.5.sp,
                        color = Color(0xFF4E342E),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        modifier = Modifier.padding(horizontal = 14.dp)
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
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                fontSize = 7.sp,
                                color = Color(0xFF555555)
                            )
                            Text(
                                text = "Ngày $currentDate",
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                fontSize = 7.sp,
                                color = Color(0xFF555555)
                            )
                        }

                        // Góc dưới bên phải: Chức vụ, chữ ký & Dấu mộc đỏ
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Việt Nam, ngày $currentDate",
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 7.sp,
                                    color = Color(0xFF333333)
                                )
                                Text(
                                    text = "Viện Trưởng",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.5.sp,
                                    color = Color(0xFF1A1A1A)
                                )
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = "Scroll Master",
                                    fontFamily = ComicFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    color = Color(0xFFD32F2F)
                                )
                            }

                            // Con dấu mộc đỏ tròn 100% Vô Tri đè lên góc chữ ký
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .offset(x = 6.dp, y = 3.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, Color(0xFFD32F2F).copy(alpha = 0.85f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "CHỨNG NHẬN\n100% VÔ TRI\n★★★",
                                    fontSize = 4.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFD32F2F),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 5.5.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

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

/**
 * Vẽ hoa văn viền Giấy Khen truyền thống đỏ - vàng bằng Compose Canvas
 */
@Composable
private fun TraditionalCertificateBorder(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val redPrimary = Color(0xFFD32F2F)
        val goldPrimary = Color(0xFFFFD54F)
        val goldDark = Color(0xFFFFB300)

        // 1. Viền ngoài cùng màu đỏ
        drawRect(
            color = redPrimary,
            topLeft = Offset(4f, 4f),
            size = Size(w - 8f, h - 8f),
            style = Stroke(width = 2.5f)
        )

        // 2. Dải khung hoa văn mạ vàng kép
        drawRect(
            color = goldPrimary,
            topLeft = Offset(10f, 10f),
            size = Size(w - 20f, h - 20f),
            style = Stroke(width = 6f)
        )

        // 3. Viền trong mảnh đỏ
        drawRect(
            color = redPrimary,
            topLeft = Offset(16f, 16f),
            size = Size(w - 32f, h - 32f),
            style = Stroke(width = 1.5f)
        )

        // 4. Vẽ 4 góc hoa văn góc vuông cổ điển (Chữ Vạn cách điệu)
        val cornerSize = 28f
        val corners = listOf(
            Offset(16f, 16f) to (1f to 1f),
            Offset(w - 16f, 16f) to (-1f to 1f),
            Offset(16f, h - 16f) to (1f to -1f),
            Offset(w - 16f, h - 16f) to (-1f to -1f)
        )

        corners.forEach { (pos, dir) ->
            val (dx, dy) = dir
            val path = Path().apply {
                moveTo(pos.x, pos.y + dy * cornerSize)
                lineTo(pos.x, pos.y)
                lineTo(pos.x + dx * cornerSize, pos.y)
                moveTo(pos.x + dx * 8f, pos.y + dy * cornerSize * 0.7f)
                lineTo(pos.x + dx * 8f, pos.y + dy * 8f)
                lineTo(pos.x + dx * cornerSize * 0.7f, pos.y + dy * 8f)
            }
            drawPath(path, redPrimary, style = Stroke(width = 2f))
        }

        // 5. Huy hiệu Quốc Huy / Cờ đỏ sao vàng ở đỉnh trên giữa
        val emblemCenterX = w / 2f
        val emblemCenterY = 12f
        val emblemRadius = 13f

        // Cánh cờ đỏ 2 bên
        val flagPath = Path().apply {
            moveTo(emblemCenterX - 36f, 16f)
            lineTo(emblemCenterX - emblemRadius, 8f)
            lineTo(emblemCenterX + emblemRadius, 8f)
            lineTo(emblemCenterX + 36f, 16f)
            lineTo(emblemCenterX + 28f, 22f)
            lineTo(emblemCenterX - 28f, 22f)
            close()
        }
        drawPath(flagPath, redPrimary)

        // Hình tròn đỏ trung tâm viền vàng
        drawCircle(redPrimary, radius = emblemRadius, center = Offset(emblemCenterX, emblemCenterY + 4f))
        drawCircle(goldDark, radius = emblemRadius, center = Offset(emblemCenterX, emblemCenterY + 4f), style = Stroke(1.5f))

        // Ngôi sao vàng 5 cánh ở giữa
        drawCircle(goldPrimary, radius = 5.5f, center = Offset(emblemCenterX, emblemCenterY + 4f))

        // 6. Họa tiết hoa sen chìm mờ ở tâm nền giấy khen
        drawCircle(
            color = goldPrimary.copy(alpha = 0.08f),
            radius = h * 0.28f,
            center = Offset(w / 2f, h / 2f)
        )
        drawCircle(
            color = redPrimary.copy(alpha = 0.04f),
            radius = h * 0.35f,
            center = Offset(w / 2f, h / 2f),
            style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
        )
    }
}

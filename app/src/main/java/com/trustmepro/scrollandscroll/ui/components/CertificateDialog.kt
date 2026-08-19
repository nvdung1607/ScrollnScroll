package com.trustmepro.scrollandscroll.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
 * Modal Bằng Khen Danh Dự thuần Vector & Canvas vẽ trực tiếp chuẩn phôi Giấy Khen truyền thống
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
                .padding(vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Khung Giấy Khen Vẽ Thuần Canvas Vector ───────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFFFDF2))
            ) {
                // 1. Lớp Canvas vẽ viền hoa văn đỏ-vàng, 4 góc cổ điển và huy hiệu cờ đỏ
                TraditionalCertificateBorder(
                    modifier = Modifier.matchParentSize()
                )

                // 2. Nội dung Text Giấy Khen căn chỉnh hoàn hảo
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 18.dp, end = 18.dp, top = 28.dp, bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header: Quốc Hiệu & Tiêu Ngữ Hài Hước Lái Đi
                    Text(
                        text = "CỘNG HÒA VÔ TRI CHIẾN THẦN VIỆT NAM",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = Color(0xFF1A1A1A),
                        letterSpacing = 0.2.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(1.dp))
                    Text(
                        text = "Độc cuộn – Tự do – Hết giấy",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.5.sp,
                        color = Color(0xFF1A1A1A),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "----------~ 🧻 ~----------",
                        fontFamily = FontFamily.Serif,
                        fontSize = 7.5.sp,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(4.dp))

                    // Tiêu đề lớn GIẤY KHEN
                    Text(
                        text = "GIẤY KHEN",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Black,
                        fontSize = 21.sp,
                        color = Color(0xFFD32F2F),
                        letterSpacing = 2.5.sp
                    )

                    Spacer(Modifier.height(2.dp))

                    // Dòng thẩm quyền ban hành
                    Text(
                        text = "VIỆN TRƯỞNG VIỆN KHOA HỌC VÔ TRI",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp,
                        color = Color(0xFF1A1A1A),
                        letterSpacing = 0.4.sp
                    )

                    Spacer(Modifier.height(4.dp))

                    // Khen tặng Chiến Thần (Bỏ lớp)
                    val displayName = if (nickname.isBlank()) "Chiến Thần Giấu Tên" else nickname
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Khen tặng Chiến Thần: ",
                            fontFamily = FontFamily.Serif,
                            fontSize = 10.sp,
                            color = Color(0xFF1A1A1A)
                        )
                        Text(
                            text = displayName,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFFD32F2F)
                        )
                    }

                    Spacer(Modifier.height(2.dp))

                    // Đạt danh hiệu & Bổ sung số mét đã cuộn
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Đạt danh hiệu: ",
                            fontFamily = FontFamily.Serif,
                            fontSize = 10.sp,
                            color = Color(0xFF1A1A1A)
                        )
                        Text(
                            text = "${badge.title} ${badge.badgeEmoji}",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFFD32F2F)
                        )
                    }

                    Spacer(Modifier.height(2.dp))

                    // Thành tích số mét cuộn
                    Text(
                        text = "Đã xuất sắc cuộn được: ${String.format(Locale.US, "%,.1f", totalMeters)} mét giấy",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp,
                        color = Color(0xFF1B5E20)
                    )

                    Spacer(Modifier.height(2.dp))

                    // Lời phê / Lời cà khịa
                    Text(
                        text = "\"${badge.description}\"",
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 8.5.sp,
                        color = Color(0xFF4E342E),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(Modifier.height(6.dp))

                    // Dòng ngày tháng, vào sổ và chữ ký / Con dấu mộc đỏ
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Góc dưới bên trái: Vào sổ khen thưởng
                        Column {
                            Text(
                                text = "Vào sổ: Số 3669/QĐ-VOTRI",
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                fontSize = 7.5.sp,
                                color = Color(0xFF555555)
                            )
                            Text(
                                text = "Ngày $currentDate",
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Italic,
                                fontSize = 7.5.sp,
                                color = Color(0xFF555555)
                            )
                        }

                        // Góc dưới bên phải: Chức vụ, chữ ký & Dấu mộc đỏ
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Việt Nam, ngày $currentDate",
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 7.5.sp,
                                    color = Color(0xFF333333)
                                )
                                Text(
                                    text = "Viện Trưởng",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.5.sp,
                                    color = Color(0xFF1A1A1A)
                                )
                                Spacer(Modifier.height(8.dp))
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
                                    .size(40.dp)
                                    .offset(x = 10.dp, y = 4.dp)
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

            Spacer(Modifier.height(12.dp))

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
 * Vẽ hoa văn viền Giấy Khen truyền thống đỏ - vàng chuẩn mẫu bằng Compose Canvas
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
            topLeft = Offset(9f, 9f),
            size = Size(w - 18f, h - 18f),
            style = Stroke(width = 5f)
        )

        // 3. Viền trong mảnh đỏ
        drawRect(
            color = redPrimary,
            topLeft = Offset(14f, 14f),
            size = Size(w - 28f, h - 28f),
            style = Stroke(width = 1.5f)
        )

        // 4. Vẽ 4 góc hoa văn góc vuông cổ điển (Chữ Vạn cách điệu)
        val cornerSize = 24f
        val corners = listOf(
            Offset(14f, 14f) to (1f to 1f),
            Offset(w - 14f, 14f) to (-1f to 1f),
            Offset(14f, h - 14f) to (1f to -1f),
            Offset(w - 14f, h - 14f) to (-1f to -1f)
        )

        corners.forEach { (pos, dir) ->
            val (dx, dy) = dir
            val path = Path().apply {
                moveTo(pos.x, pos.y + dy * cornerSize)
                lineTo(pos.x, pos.y)
                lineTo(pos.x + dx * cornerSize, pos.y)
                moveTo(pos.x + dx * 6f, pos.y + dy * cornerSize * 0.7f)
                lineTo(pos.x + dx * 6f, pos.y + dy * 6f)
                lineTo(pos.x + dx * cornerSize * 0.7f, pos.y + dy * 6f)
            }
            drawPath(path, redPrimary, style = Stroke(width = 2f))
        }

        // 5. Cụm Huy hiệu Cờ Đỏ Sao Vàng xòe quạt chuẩn mẫu ảnh
        val emblemCenterX = w / 2f
        val emblemCenterY = 13f
        val emblemRadius = 11f

        // Cánh cờ đỏ xếp tầng 2 bên
        val leftFlagPath = Path().apply {
            moveTo(emblemCenterX - 40f, 15f)
            lineTo(emblemCenterX - emblemRadius, 7f)
            lineTo(emblemCenterX - emblemRadius, 19f)
            lineTo(emblemCenterX - 35f, 21f)
            close()
        }
        val rightFlagPath = Path().apply {
            moveTo(emblemCenterX + 40f, 15f)
            lineTo(emblemCenterX + emblemRadius, 7f)
            lineTo(emblemCenterX + emblemRadius, 19f)
            lineTo(emblemCenterX + 35f, 21f)
            close()
        }
        drawPath(leftFlagPath, redPrimary)
        drawPath(rightFlagPath, redPrimary)

        // Hình tròn đỏ trung tâm viền vàng
        drawCircle(redPrimary, radius = emblemRadius, center = Offset(emblemCenterX, emblemCenterY))
        drawCircle(goldDark, radius = emblemRadius, center = Offset(emblemCenterX, emblemCenterY), style = Stroke(1.5f))

        // Ngôi sao vàng ở tâm huy hiệu
        drawCircle(goldPrimary, radius = 4.5f, center = Offset(emblemCenterX, emblemCenterY))

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

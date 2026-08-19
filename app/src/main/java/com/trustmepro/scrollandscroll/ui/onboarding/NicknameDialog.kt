package com.trustmepro.scrollandscroll.ui.onboarding

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trustmepro.scrollandscroll.ui.components.ComicCircleButton
import com.trustmepro.scrollandscroll.ui.components.ComicInkBlack
import com.trustmepro.scrollandscroll.ui.components.ComicOrange
import com.trustmepro.scrollandscroll.ui.components.ComicYellow
import com.trustmepro.scrollandscroll.ui.components.M3PrimaryButton
import com.trustmepro.scrollandscroll.ui.theme.ComicFontFamily

private val RANDOM_NAMES = listOf(
    "Chiến Thần WC",
    "Thánh Cuộn Giấy",
    "Sếp Tới Tắt App",
    "Vua Lười 2026",
    "Alien Vô Tri",
    "Ninja Cuộn Êm",
    "Tay To Hơn Chân",
    "Trùm Đốt Giờ Làm",
    "Gia Đình 4 Đời Cuộn Giấy",
    "Bà Bán Xôi Đầu Ngõ",
    "Kẻ Hủy Diệt Cường Lực",
    "Vận Động Viên Trùm Chăn",
    "Toilet Master 3000",
    "Ngón Tay Dẻo Kẹo",
    "Cuộn Xong Đi Ngủ"
)

/**
 * Dialog chào mừng tân thủ và nhập biệt danh (Nickname)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NicknameDialog(
    currentNickname: String = "",
    onConfirm: (String) -> Unit
) {
    var nicknameInput by remember { mutableStateOf(currentNickname.ifBlank { RANDOM_NAMES.random() }) }

    BasicAlertDialog(
        onDismissRequest = { /* Bắt buộc nhập tên hoặc dùng tên mặc định */ }
    ) {
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
                // Icon Avatar
                Text(text = "👑", fontSize = 48.sp)

                Spacer(Modifier.height(4.dp))

                // Tiêu đề
                Text(
                    text = "DANH XƯNG CHIẾN THẦN",
                    fontFamily = ComicFontFamily,
                    fontSize = 20.sp,
                    color = ComicInkBlack
                )

                Text(
                    text = "Hãy chọn cho mình một biệt danh vô tri để ghi danh vào Bảng Vàng toàn cầu!",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = ComicInkBlack.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )

                Spacer(Modifier.height(14.dp))

                // Input + Nút Xúc Xắc Random 🎲
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = nicknameInput,
                        onValueChange = { if (it.length <= 25) nicknameInput = it },
                        placeholder = { Text("Nhập biệt danh...") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = ComicInkBlack,
                            unfocusedBorderColor = ComicInkBlack.copy(alpha = 0.4f),
                            focusedTextColor = ComicInkBlack,
                            unfocusedTextColor = ComicInkBlack
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(Modifier.width(8.dp))

                    // Nút xúc xắc đổi tên ngẫu nhiên
                    ComicCircleButton(
                        label = "",
                        containerColor = ComicYellow,
                        onClick = { nicknameInput = RANDOM_NAMES.random() },
                        size = 48.dp
                    ) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = "Random Name",
                            tint = ComicInkBlack,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                // Nút Xác Nhận
                M3PrimaryButton(
                    onClick = {
                        val finalName = nicknameInput.trim().ifBlank { RANDOM_NAMES.random() }
                        onConfirm(finalName)
                    },
                    containerColor = ComicOrange,
                    contentColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🚀 BẮT ĐẦU CUỘN NGAY!",
                        fontFamily = ComicFontFamily,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

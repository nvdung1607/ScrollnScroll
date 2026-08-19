package com.trustmepro.scrollandscroll.data.model

import androidx.compose.ui.graphics.Color
import com.trustmepro.scrollandscroll.ui.theme.Gold24K
import com.trustmepro.scrollandscroll.ui.theme.OverdriveFire
import com.trustmepro.scrollandscroll.ui.theme.PurpleGalaxy
import com.trustmepro.scrollandscroll.ui.theme.SoftPink

/**
 * 9 cấp độ Skin cuộn giấy tiến hóa theo số mét cuộn
 * Được cân đối số mét hấp dẫn, dễ mở khóa theo từng mốc chơi
 */
enum class SkinType(
    val id: String,
    val displayName: String,
    val description: String,
    val requiredMeters: Double,
    val primaryColor: Color,
    val accentColor: Color,
    val patternEmoji: String,
    val bannerText: String = ""
) {
    SCHOOL_CANTEEN(
        id = "SCHOOL_CANTEEN",
        displayName = "Giấy Căn Tin Trường Học",
        description = "Giấy 1 lớp thô sơ màu xám nhạt, sột soạt cổ điển",
        requiredMeters = 0.0,
        primaryColor = Color(0xFFF5F5F5),
        accentColor = Color(0xFFE0E0E0),
        patternEmoji = "🧻",
        bannerText = "1-PLY"
    ),
    ROSE_PETAL(
        id = "ROSE_PETAL",
        displayName = "Giấy Hoa Hồng Thơm Ngát",
        description = "Màu hồng phấn êm dịu, in họa tiết cánh hoa rơi",
        requiredMeters = 25.0,
        primaryColor = Color(0xFFFFE4E9),
        accentColor = Color(0xFFFFB2C9),
        patternEmoji = "🌸",
        bannerText = "ROSE"
    ),
    COMIC_STRIP(
        id = "COMIC_STRIP",
        displayName = "Giấy Truyện Tranh 4 Khung",
        description = "Mỗi đoạn giấy trôi qua là một mẩu truyện meme cười vui",
        requiredMeters = 75.0,
        primaryColor = Color(0xFFFFF9C4),
        accentColor = Color(0xFFFFE082),
        patternEmoji = "📰",
        bannerText = "MEME"
    ),
    ROYAL_GOLD_24K(
        id = "ROYAL_GOLD_24K",
        displayName = "Giấy Dát Vàng Hoàng Gia 24K",
        description = "Vàng óng ánh quý tộc, rơi bụi vàng lấp lánh khi vuốt",
        requiredMeters = 200.0,
        primaryColor = Color(0xFFFFE082),
        accentColor = Color(0xFFFFB300),
        patternEmoji = "👑",
        bannerText = "24K GOLD"
    ),
    NOODLE_SOUP(
        id = "NOODLE_SOUP",
        displayName = "Giấy Tô Bún Riêu Cua",
        description = "Họa tiết sợi bún, chả cua, ớt cay, âm thanh xì xụp vui tai",
        requiredMeters = 500.0,
        primaryColor = Color(0xFFFFE0B2),
        accentColor = Color(0xFFFFB74D),
        patternEmoji = "🍜",
        bannerText = "RAMEN"
    ),
    DOLLAR_100(
        id = "DOLLAR_100",
        displayName = "Giấy Tiền Đô La $100",
        description = "Dải polyme in cọc tiền USD, nghe tiếng đếm tiền xoẹt xoẹt",
        requiredMeters = 1200.0,
        primaryColor = Color(0xFFC8E6C9),
        accentColor = Color(0xFF81C784),
        patternEmoji = "💵",
        bannerText = "$100 USD"
    ),
    MUMMY_BANDAGE(
        id = "MUMMY_BANDAGE",
        displayName = "Băng Gạc Xác Ướp Ai Cập",
        description = "Băng cổ xưa quấn xác ướp Pharaoh ngàn năm",
        requiredMeters = 3000.0,
        primaryColor = Color(0xFFEFEBE9),
        accentColor = Color(0xFFBCAAA4),
        patternEmoji = "🏜️",
        bannerText = "PHARAOH"
    ),
    SPACE_GALAXY(
        id = "SPACE_GALAXY",
        displayName = "Giấy Galaxy Không Gian",
        description = "Dải ngân hà tinh vân huyền ảo, vệt sáng neon vũ trụ",
        requiredMeters = 7500.0,
        primaryColor = Color(0xFF281B4B),
        accentColor = Color(0xFF673AB7),
        patternEmoji = "🌌",
        bannerText = "GALAXY"
    ),
    ULTIMATE_DIAMOND(
        id = "ULTIMATE_DIAMOND",
        displayName = "Giấy Kim Cương Tối Thượng",
        description = "Trong suốt lấp lánh phát quang, hào quang thiên thần ca",
        requiredMeters = 20000.0,
        primaryColor = Color(0xFFE0F7FA),
        accentColor = Color(0xFF00E5FF),
        patternEmoji = "💎",
        bannerText = "DIAMOND"
    );

    companion object {
        fun getById(id: String): SkinType {
            return entries.find { it.id == id } ?: SCHOOL_CANTEEN
        }
    }
}

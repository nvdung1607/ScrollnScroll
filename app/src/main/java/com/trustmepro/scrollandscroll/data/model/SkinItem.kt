package com.trustmepro.scrollandscroll.data.model

import androidx.compose.ui.graphics.Color
import com.trustmepro.scrollandscroll.ui.theme.Gold24K
import com.trustmepro.scrollandscroll.ui.theme.OverdriveFire
import com.trustmepro.scrollandscroll.ui.theme.PrimaryLight
import com.trustmepro.scrollandscroll.ui.theme.PurpleGalaxy
import com.trustmepro.scrollandscroll.ui.theme.SecondaryLight
import com.trustmepro.scrollandscroll.ui.theme.SoftPink
import com.trustmepro.scrollandscroll.ui.theme.TertiaryLight

/**
 * 9 cấp độ Skin cuộn giấy tiến hóa theo số mét
 */
enum class SkinType(
    val id: String,
    val displayName: String,
    val description: String,
    val requiredMeters: Double,
    val primaryColor: Color,
    val accentColor: Color,
    val patternEmoji: String
) {
    SCHOOL_CANTEEN(
        id = "SCHOOL_CANTEEN",
        displayName = "Giấy Căn Tin Trường Học",
        description = "Giấy 1 lớp thô sơ màu xám nhạt, sột soạt cổ điển",
        requiredMeters = 0.0,
        primaryColor = Color(0xFFE5E5E5),
        accentColor = Color(0xFFBDBDBD),
        patternEmoji = "🧻"
    ),
    ROSE_PETAL(
        id = "ROSE_PETAL",
        displayName = "Giấy Hoa Hồng Thơm Ngát",
        description = "Màu hồng phấn êm dịu, in họa tiết cánh hoa rơi",
        requiredMeters = 500.0,
        primaryColor = Color(0xFFFFD1DC),
        accentColor = SoftPink,
        patternEmoji = "🌸"
    ),
    COMIC_STRIP(
        id = "COMIC_STRIP",
        displayName = "Giấy Truyện Tranh 4 Khung",
        description = "Mỗi đoạn giấy trôi qua là một mẩu truyện meme cười vui",
        requiredMeters = 2000.0,
        primaryColor = Color(0xFFFFF9C4),
        accentColor = Color(0xFF37474F),
        patternEmoji = "📰"
    ),
    ROYAL_GOLD_24K(
        id = "ROYAL_GOLD_24K",
        displayName = "Giấy Dát Vàng Hoàng Gia 24K",
        description = "Vàng óng ánh quý tộc, rơi bụi vàng lấp lánh khi vuốt",
        requiredMeters = 10000.0,
        primaryColor = Gold24K,
        accentColor = PrimaryLight,
        patternEmoji = "👑"
    ),
    NOODLE_SOUP(
        id = "NOODLE_SOUP",
        displayName = "Giấy Tô Bún Riêu Cua",
        description = "Họa tiết sợi bún, chả cua, ớt cay, âm thanh xì xụp vui tai",
        requiredMeters = 50000.0,
        primaryColor = Color(0xFFFFCCBC),
        accentColor = SecondaryLight,
        patternEmoji = "🍜"
    ),
    DOLLAR_100(
        id = "DOLLAR_100",
        displayName = "Giấy Tiền Đô La $100",
        description = "Dải polyme in cọc tiền USD, nghe tiếng đếm tiền xoẹt xoẹt",
        requiredMeters = 100000.0,
        primaryColor = Color(0xFFC8E6C9),
        accentColor = TertiaryLight,
        patternEmoji = "💵"
    ),
    MUMMY_BANDAGE(
        id = "MUMMY_BANDAGE",
        displayName = "Băng Gạc Xác Ướp Ai Cập",
        description = "Băng cổ xưa quấn xác ướp Pharaoh ngàn năm",
        requiredMeters = 250000.0,
        primaryColor = Color(0xFFD7CCC8),
        accentColor = Color(0xFF8D6E63),
        patternEmoji = "🏜️"
    ),
    SPACE_GALAXY(
        id = "SPACE_GALAXY",
        displayName = "Giấy Galaxy Không Gian",
        description = "Dải ngân hà tinh vân huyền ảo, hiệu ứng vệt sáng neon vũ trụ",
        requiredMeters = 500000.0,
        primaryColor = Color(0xFF311B92),
        accentColor = PurpleGalaxy,
        patternEmoji = "🌌"
    ),
    ULTIMATE_DIAMOND(
        id = "ULTIMATE_DIAMOND",
        displayName = "Giấy Kim Cương Tối Thượng",
        description = "Trong suốt lấp lánh phát quang, hào quang thiên thần ca",
        requiredMeters = 999999.0,
        primaryColor = Color(0xFFE0F7FA),
        accentColor = Color(0xFF00E5FF),
        patternEmoji = "💎"
    );

    companion object {
        fun getById(id: String): SkinType {
            return entries.find { it.id == id } ?: SCHOOL_CANTEEN
        }
    }
}

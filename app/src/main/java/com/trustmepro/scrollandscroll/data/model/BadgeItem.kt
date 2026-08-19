package com.trustmepro.scrollandscroll.data.model

/**
 * 8 danh hiệu trào phúng kèm lời nhận xét cà khịa
 */
enum class BadgeType(
    val id: String,
    val title: String,
    val description: String,
    val requiredMeters: Double,
    val badgeEmoji: String
) {
    BEGINNER_FINGER(
        id = "BEGINNER_FINGER",
        title = "Tân Binh Ngón Tay Dẻo",
        description = "Mới vuốt có tí đã mỏi tay rồi à bạn ơi?",
        requiredMeters = 100.0,
        badgeEmoji = "👶"
    ),
    SEMI_PRO_ROLLER(
        id = "SEMI_PRO_ROLLER",
        title = "Kẻ Tiêu Thụ Giấy Chuyên Nghiệp",
        description = "Đủ lượng giấy cho một hộ gia đình dùng cả năm trời.",
        requiredMeters = 1000.0,
        badgeEmoji = "🧻"
    ),
    WORK_HOUR_BURNER(
        id = "WORK_HOUR_BURNER",
        title = "Chiến Thần Đốt Giờ Làm",
        description = "Sếp bạn đang nhìn kìa, nhưng cuộn giấy này cuốn hơn!",
        requiredMeters = 5000.0,
        badgeEmoji = "💼"
    ),
    SCREEN_DESTROYER(
        id = "SCREEN_DESTROYER",
        title = "Kẻ Hủy Diệt Cường Lực Điện Thoại",
        description = "Miếng dán màn hình của bạn sắp mòn vẹt rồi đấy!",
        requiredMeters = 20000.0,
        badgeEmoji = "📱"
    ),
    LEGENDARY_UNEMPLOYED(
        id = "LEGENDARY_UNEMPLOYED",
        title = "Thất Nghiệp Huyền Thoại",
        description = "Thời gian bạn vuốt chỗ này đủ để học xong 2 khóa IELTS rồi.",
        requiredMeters = 50000.0,
        badgeEmoji = "🛋️"
    ),
    NATIONAL_ATHLETE(
        id = "NATIONAL_ATHLETE",
        title = "Vận Động Viên Cuộn Giấy Cấp Quốc Gia",
        description = "Ngón tay trỏ đã to gấp đôi ngón cái sau bao nỗ lực vô tri.",
        requiredMeters = 100000.0,
        badgeEmoji = "🥇"
    ),
    PEAK_ABSURDITY(
        id = "PEAK_ABSURDITY",
        title = "Đỉnh Cao Tiến Hóa Của Sự Vô Tri",
        description = "Người ngoài hành tinh đang quan sát bạn trong sự kinh ngạc.",
        requiredMeters = 500000.0,
        badgeEmoji = "👽"
    ),
    IMMORTAL_SCROLLER(
        id = "IMMORTAL_SCROLLER",
        title = "Đắc Đạo Thành Tiên Cuộn Giấy",
        description = "Bạn đã hoàn thành trò chơi. Bây giờ hãy đi ngủ đi!",
        requiredMeters = 999999.0,
        badgeEmoji = "🧘"
    );

    companion object {
        fun getById(id: String): BadgeType? {
            return entries.find { it.id == id }
        }

        fun getHighestUnlocked(totalMeters: Double): BadgeType? {
            return entries.filter { totalMeters >= it.requiredMeters }
                .maxByOrNull { it.requiredMeters }
        }
    }
}

package com.trustmepro.scrollandscroll.data.model

/**
 * 8 danh hiệu trào phúng ứng với 8 mốc mét meme hài hước
 */
enum class BadgeType(
    val id: String,
    val title: String,
    val description: String,
    val requiredMeters: Double,
    val badgeEmoji: String
) {
    BEGINNER_36(
        id = "BEGINNER_36",
        title = "Chiến Thần Rau Má 36",
        description = "Mới vuốt 36m đã đòi uống sinh tố rau má đậu xanh mát lạnh!",
        requiredMeters = 36.0,
        badgeEmoji = "🍃"
    ),
    MASTER_69(
        id = "MASTER_69",
        title = "Chiến Thần Giác Ngộ 69",
        description = "Kỹ năng vuốt cuộn giấy đã đạt cảnh giới tinh tế 69.",
        requiredMeters = 69.0,
        badgeEmoji = "😏"
    ),
    PROSPERITY_168(
        id = "PROSPERITY_168",
        title = "Thần Tài Nhất Lộc Phát 168",
        description = "Càng cuộn càng phát tài, sếp nhìn cũng phải nể!",
        requiredMeters = 168.0,
        badgeEmoji = "👑"
    ),
    SUPER_CHILL_420(
        id = "SUPER_CHILL_420",
        title = "Chúa Tể 420 Siêu Chill",
        description = "Vuốt giấy chậm rãi như đang thưởng thức tô mì tôm chua cay.",
        requiredMeters = 420.0,
        badgeEmoji = "🍜"
    ),
    JACKPOT_777(
        id = "JACKPOT_777",
        title = "Đại Gia Nổ Hũ 777",
        description = "Năng lượng Jackpot tràn trề, tiêu tiền như rải giấy vệ sinh.",
        requiredMeters = 777.0,
        badgeEmoji = "💵"
    ),
    ETERNAL_1314(
        id = "ETERNAL_1314",
        title = "Xác Ướp Chung Tình 1314",
        description = "Trọn đời trọn kiếp 1314m, quấn chặt lấy cuộn giấy không rời.",
        requiredMeters = 1314.0,
        badgeEmoji = "🏜️"
    ),
    COSMIC_9999(
        id = "COSMIC_9999",
        title = "Phi Hành Gia Tứ Quý 9",
        description = "Dải giấy đã vươn tầm vũ trụ, người ngoài hành tinh cũng xin bái phục.",
        requiredMeters = 9999.0,
        badgeEmoji = "🌌"
    ),
    ULTIMATE_69420(
        id = "ULTIMATE_69420",
        title = "Đắc Đạo Meme Chúa 69420",
        description = "Huyền thoại vô tri tối thượng của nhân loại. Bạn có thể tự hào đi ngủ rồi!",
        requiredMeters = 69420.0,
        badgeEmoji = "💎"
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

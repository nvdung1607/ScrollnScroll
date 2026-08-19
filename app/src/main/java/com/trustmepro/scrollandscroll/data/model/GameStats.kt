package com.trustmepro.scrollandscroll.data.model

/**
 * Model chứa toàn bộ trạng thái dữ liệu của người chơi
 */
data class GameStats(
    val totalMeters: Double = 0.0,
    val totalSwipes: Long = 0L,
    val currentSps: Float = 0f,
    val isOverdrive: Boolean = false,
    val selectedSkinId: String = SkinType.SCHOOL_CANTEEN.id,
    val nickname: String = "",
    val isSoundEnabled: Boolean = true,
    val isHapticEnabled: Boolean = true,
    val unlockedSkinIds: Set<String> = setOf(SkinType.SCHOOL_CANTEEN.id),
    val unlockedBadgeIds: Set<String> = emptySet(),
    val newlyUnlockedBadge: BadgeType? = null
) {
    val selectedSkin: SkinType
        get() = SkinType.getById(selectedSkinId)

    val highestBadge: BadgeType?
        get() = BadgeType.getHighestUnlocked(totalMeters)

    val nextSkin: SkinType?
        get() = SkinType.entries.firstOrNull { it.requiredMeters > totalMeters }

    val nextSkinProgress: Float
        get() {
            val next = nextSkin ?: return 1.0f
            val currentSkinMeters = SkinType.entries
                .filter { it.requiredMeters <= totalMeters }
                .maxOfOrNull { it.requiredMeters } ?: 0.0
            val target = next.requiredMeters - currentSkinMeters
            val current = totalMeters - currentSkinMeters
            return (current / target).toFloat().coerceIn(0f, 1f)
        }
}

package com.trustmepro.scrollandscroll.data.repository

import com.trustmepro.scrollandscroll.data.model.CountryRank
import com.trustmepro.scrollandscroll.data.model.LeaderboardUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Locale

interface ILeaderboardRepository {
    fun getDailyLeaderboard(currentUserMeters: Double, currentNickname: String): Flow<List<LeaderboardUser>>
    fun getAllTimeLeaderboard(currentUserMeters: Double, currentNickname: String): Flow<List<LeaderboardUser>>
    fun getCountryBattleLeaderboard(currentUserMeters: Double): Flow<List<CountryRank>>
    suspend fun syncScoreToServer(nickname: String, meters: Double, countryCode: String): Boolean
}

/**
 * Repository cung cấp dữ liệu Bảng Xếp Hạng.
 * Hiện đang cấu hình Mock Data phong phú, hài hước và sẵn sàng kết nối Firebase Firestore sau này.
 */
class LeaderboardRepository : ILeaderboardRepository {

    override fun getDailyLeaderboard(
        currentUserMeters: Double,
        currentNickname: String
    ): Flow<List<LeaderboardUser>> = flow {
        // Mô phỏng độ trễ tải mạng
        delay(150)

        val mockPlayers = listOf(
            Triple("Thánh Cuộn Giấy Hà Đông", "🇻🇳", 48520.5),
            Triple("Sếp Tới Thì Tắt App", "🇻🇳", 42150.2),
            Triple("Elon Scroll", "🇺🇸", 39800.0),
            Triple("Ninja Cuộn Không Tiếng Động", "🇯🇵", 36420.8),
            Triple("Bà Bán Xôi Đầu Ngõ", "🇻🇳", 31200.4),
            Triple("Kẻ Hủy Diệt Màn Hình", "🇰🇷", 28900.1),
            Triple("Chiến Thần Đốt Giờ Làm", "🇻🇳", 25400.0),
            Triple("Toilet Master 3000", "🇩🇪", 22100.5),
            Triple("Cuộn Xong Đi Ngủ", "🇻🇳", 19800.2),
            Triple("Vua Lười 2026", "🇫🇷", 17500.0),
            Triple("Chủ Tịch Giấu Tên", "🇻🇳", 15200.3),
            Triple("Alien 51 Toilet", "🇧🇷", 13400.0),
            Triple("Tay Phải To Hơn Tay Trái", "🇻🇳", 11200.8),
            Triple("Gia Đình 4 Đời Cuộn Giấy", "🇻🇳", 9800.0),
            Triple("Troll Sếp Mọi Lúc", "🇹🇭", 8400.5),
            Triple("Ngón Tay Không Biết Mệt", "🇮🇩", 7100.2),
            Triple("Cuộn Giấy Thay Vì Chạy Bộ", "🇻🇳", 5800.0),
            Triple("Học Sinh Chăm Chỉ (Trong WC)", "🇻🇳", 4600.5),
            Triple("Vận Động Viên Trùm Chăn", "🇻🇳", 3200.0),
            Triple("Mới Vào Chơi Thôi", "🇻🇳", 1800.4)
        )

        val users = mutableListOf<LeaderboardUser>()

        // Thêm người chơi hiện tại
        val currentName = if (currentNickname.isBlank()) "Bạn (Chiến Thần)" else "$currentNickname (Bạn)"
        val myUser = LeaderboardUser(
            rank = 0,
            userId = "current_user",
            nickname = currentName,
            countryCode = "VN",
            countryFlag = "🇻🇳",
            totalMeters = currentUserMeters,
            skinEmoji = "🧻",
            badgeTitle = "Chiến Thần Tự Do",
            isCurrentUser = true
        )
        users.add(myUser)

        // Thêm danh sách mock
        mockPlayers.forEachIndexed { index, (name, flag, meters) ->
            users.add(
                LeaderboardUser(
                    rank = index + 1,
                    userId = "mock_$index",
                    nickname = name,
                    countryCode = if (flag == "🇻🇳") "VN" else "GLOBAL",
                    countryFlag = flag,
                    totalMeters = meters,
                    skinEmoji = listOf("🧻", "🌸", "👑", "💵", "🍜", "💎").random(),
                    badgeTitle = "Top Cuộn",
                    isCurrentUser = false
                )
            )
        }

        // Sắp xếp theo số mét giảm dần và đánh lại thứ hạng Rank
        val sortedList = users.sortedByDescending { it.totalMeters }
            .mapIndexed { index, user ->
                user.copy(rank = index + 1)
            }

        emit(sortedList)
    }

    override fun getAllTimeLeaderboard(
        currentUserMeters: Double,
        currentNickname: String
    ): Flow<List<LeaderboardUser>> = flow {
        delay(150)

        val mockPlayers = listOf(
            Triple("Cụ Tổ Ngành Cuộn Giấy", "🇻🇳", 985200.0),
            Triple("Grandmaster Toilet", "🇺🇸", 874100.5),
            Triple("Thần Sấm Cuộn Giấy", "🇩🇪", 762000.0),
            Triple("Bậc Thầy Vô Tri Quốc Tế", "🇯🇵", 654200.8),
            Triple("Nữ Hoàng Giấy 24K", "🇫🇷", 543100.0),
            Triple("Chiến Thần Xuyên Lục Địa", "🇧🇷", 432000.4),
            Triple("Thánh Ngón Tay Vàng", "🇰🇷", 321900.0),
            Triple("Trùm Cuộn Xuyên Đêm", "🇻🇳", 254800.5),
            Triple("Vô Địch Cuộn Giấy Đông Nam Á", "🇹🇭", 198700.0),
            Triple("Thợ Săn Kỷ Lục Guinness", "🇮🇩", 145200.0)
        )

        val users = mutableListOf<LeaderboardUser>()

        val currentName = if (currentNickname.isBlank()) "Bạn (Chiến Thần)" else "$currentNickname (Bạn)"
        users.add(
            LeaderboardUser(
                rank = 0,
                userId = "current_user",
                nickname = currentName,
                countryCode = "VN",
                countryFlag = "🇻🇳",
                totalMeters = currentUserMeters,
                skinEmoji = "👑",
                badgeTitle = "Chiến Thần Toàn Cầu",
                isCurrentUser = true
            )
        )

        mockPlayers.forEachIndexed { index, (name, flag, meters) ->
            users.add(
                LeaderboardUser(
                    rank = index + 1,
                    userId = "alltime_$index",
                    nickname = name,
                    countryCode = "GLOBAL",
                    countryFlag = flag,
                    totalMeters = meters,
                    skinEmoji = listOf("👑", "💎", "🌌", "💵").random(),
                    badgeTitle = "Huyền Thoại",
                    isCurrentUser = false
                )
            )
        }

        val sortedList = users.sortedByDescending { it.totalMeters }
            .mapIndexed { index, user ->
                user.copy(rank = index + 1)
            }

        emit(sortedList)
    }

    override fun getCountryBattleLeaderboard(currentUserMeters: Double): Flow<List<CountryRank>> = flow {
        delay(150)

        val countries = listOf(
            CountryRank(1, "VN", "Việt Nam", "🇻🇳", 15842000.0 + currentUserMeters, 452100),
            CountryRank(2, "US", "Hoa Kỳ", "🇺🇸", 14210000.0, 382000),
            CountryRank(3, "JP", "Nhật Bản", "🇯🇵", 12540000.0, 310500),
            CountryRank(4, "BR", "Brazil", "🇧🇷", 9820000.0, 245000),
            CountryRank(5, "KR", "Hàn Quốc", "🇰🇷", 8450000.0, 198000),
            CountryRank(6, "DE", "Đức", "🇩🇪", 7120000.0, 164000),
            CountryRank(7, "FR", "Pháp", "🇫🇷", 5890000.0, 132000),
            CountryRank(8, "TH", "Thái Lan", "🇹🇭", 4920000.0, 110000),
            CountryRank(9, "ID", "Indonesia", "🇮🇩", 3850000.0, 95000),
            CountryRank(10, "GB", "Vương Quốc Anh", "🇬🇧", 2980000.0, 78000)
        )

        emit(countries)
    }

    override suspend fun syncScoreToServer(
        nickname: String,
        meters: Double,
        countryCode: String
    ): Boolean {
        // Sẵn sàng kết nối Firebase Firestore sau này:
        // db.collection("leaderboard").document(userId).set(...)
        return true
    }
}

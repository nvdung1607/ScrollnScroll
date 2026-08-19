package com.trustmepro.scrollandscroll.ui.navigation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.trustmepro.scrollandscroll.ui.cabinet.CabinetScreen
import com.trustmepro.scrollandscroll.ui.game.GameScreen
import com.trustmepro.scrollandscroll.ui.game.GameViewModel
import com.trustmepro.scrollandscroll.ui.leaderboard.LeaderboardScreen
import com.trustmepro.scrollandscroll.ui.onboarding.NicknameDialog
import com.trustmepro.scrollandscroll.ui.settings.SettingsDialog

/**
 * Điều hướng chính toàn bộ ứng dụng (Game Screen, Leaderboard, Cabinet, Dialogs)
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    gameViewModel: GameViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by gameViewModel.uiState.collectAsState()

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showNicknameDialog by remember { mutableStateOf(false) }
    var backPressTime by remember { mutableStateOf(0L) }

    // Kiểm tra hiển thị Onboarding nếu người dùng chưa có nickname
    var hasCheckedOnboarding by remember { mutableStateOf(false) }
    if (!hasCheckedOnboarding && uiState.nickname.isBlank() && uiState.totalSwipes > 0) {
        showNicknameDialog = true
        hasCheckedOnboarding = true
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Game.route,
        modifier = Modifier.fillMaxSize(),
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(350)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(350)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(350)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(350)
            )
        }
    ) {
        // ── 1. Màn hình Game Chính ───────────────────────────────────────────
        composable(Screen.Game.route) {
            // Xử lý nút Back troll người dùng
            BackHandler {
                val now = System.currentTimeMillis()
                if (now - backPressTime > 2000) {
                    backPressTime = now
                    Toast.makeText(
                        context,
                        "🧻 Cuộn nốt 100m nữa đi, sao nản lòng sớm thế! Nhấn lần nữa để thoát.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    (context as? android.app.Activity)?.finish()
                }
            }

            GameScreen(
                viewModel = gameViewModel,
                onNavigateToLeaderboard = {
                    navController.navigate(Screen.Leaderboard.route)
                },
                onNavigateToCabinet = { tab ->
                    navController.navigate(Screen.Cabinet.createRoute(tab))
                },
                onOpenSettings = {
                    showSettingsDialog = true
                }
            )
        }

        // ── 2. Màn hình Bảng Xếp Hạng ────────────────────────────────────────
        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // ── 3. Màn hình Tủ Đồ Skin & Danh Hiệu ──────────────────────────────
        composable(
            route = Screen.Cabinet.route,
            arguments = listOf(
                androidx.navigation.navArgument("tab") {
                    type = androidx.navigation.NavType.StringType
                    defaultValue = "skins"
                }
            )
        ) { backStackEntry ->
            val tabStr = backStackEntry.arguments?.getString("tab") ?: "skins"
            val initialTab = if (tabStr.equals("badges", ignoreCase = true)) {
                com.trustmepro.scrollandscroll.ui.cabinet.CabinetTab.BADGES
            } else {
                com.trustmepro.scrollandscroll.ui.cabinet.CabinetTab.SKINS
            }
            CabinetScreen(
                initialTab = initialTab,
                onBack = { navController.popBackStack() }
            )
        }
    }

    // ── Dialog Cài Đặt (Settings) ────────────────────────────────────────────
    if (showSettingsDialog) {
        SettingsDialog(
            isSoundEnabled = uiState.isSoundEnabled,
            isHapticEnabled = uiState.isHapticEnabled,
            nickname = uiState.nickname,
            onToggleSound = { gameViewModel.toggleSound() },
            onToggleHaptic = { gameViewModel.toggleHaptic() },
            onChangeNickname = {
                showSettingsDialog = false
                showNicknameDialog = true
            },
            onDismiss = { showSettingsDialog = false }
        )
    }

    // ── Dialog Nhập Tên (Nickname Onboarding) ────────────────────────────────
    if (showNicknameDialog) {
        NicknameDialog(
            currentNickname = uiState.nickname,
            onConfirm = { newName ->
                gameViewModel.setNickname(newName)
                showNicknameDialog = false
                Toast.makeText(context, "Chào mừng Chiến Thần $newName gia nhập!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

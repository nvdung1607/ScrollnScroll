package com.trustmepro.scrollandscroll.ui.navigation

sealed class Screen(val route: String) {
    data object Game : Screen("game")
    data object Leaderboard : Screen("leaderboard")
    data object Cabinet : Screen("cabinet/{tab}") {
        fun createRoute(tab: String = "skins"): String = "cabinet/$tab"
    }
}

package com.example.fitalog.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
    object Dashboard : Screen("dashboard")
    object Session : Screen("session/{workoutId}") {
        fun createRoute(workoutId: Int) = "session/$workoutId"
    }
}


sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: String
) {
    object NutriNote : BottomNavItem("tab_nutrinote", "NutriNote", "restaurant")
    object SleepSync : BottomNavItem("tab_sleepsync", "SleepSync", "bedtime")
    object Workit : BottomNavItem("tab_workit", "Workit", "fitness_center")
    object Profile : BottomNavItem("tab_profile", "Profile", "person")
}


sealed class ScreenSleep(val route: String) {
    object Summary : ScreenSleep("sleep_summary")
    object NonActive : ScreenSleep("sleep_non_active")
    object Active : ScreenSleep("sleep_active")
    object Complete : ScreenSleep("sleep_complete")
    object Profile : ScreenSleep("sleep_profile")
    object History : ScreenSleep("sleep_history")
}


sealed class ScreenNutri(val route: String) {
    object Home : ScreenNutri("nutri_home")
    object AddFood : ScreenNutri("nutri_add/{meal}") {
        fun createRoute(meal: String) = "nutri_add/$meal"
    }
}


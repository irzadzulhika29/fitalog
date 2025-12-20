package com.example.fitalog.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.fitalog.model.DailyProgress
import com.example.fitalog.model.DailyStats
import com.example.fitalog.model.SleepSyncState
import com.example.fitalog.model.Workout
import com.example.fitalog.model.WorkoutLog
import com.example.fitalog.ui.screen.nutrinote.NutriHomeScreen
import com.example.fitalog.ui.screen.nutrinote.NutriViewModel
import com.example.fitalog.ui.screen.sleepsync.SummaryScreen
import com.example.fitalog.ui.screen.workit.DashboardScreen

data class BottomNavItemData(
    val title: String,
    val icon: ImageVector,
    val selectedColor: Color
)

@Composable
fun MainScreen(
    nutriViewModel: NutriViewModel,
    onAddFoodClick: (String) -> Unit,
    sleepState: SleepSyncState,
    onStartSleepTracking: () -> Unit,
    onViewSleepHistory: () -> Unit,
    stats: DailyStats,
    progress: List<DailyProgress>,
    workouts: List<Workout>,
    logs: List<WorkoutLog>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onWorkoutClick: (Int) -> Unit,
    onDeleteLog: (WorkoutLog) -> Unit,
    onDeleteAllLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    val navItems = listOf(
        BottomNavItemData("NutriNote", Icons.Default.Restaurant, Color(0xFF4CAF50)),
        BottomNavItemData("SleepSync", Icons.Default.Bedtime, Color(0xFF7C4DFF)),
        BottomNavItemData("Workit", Icons.Default.FitnessCenter, Color(0xFFFF5722)),
        BottomNavItemData("Profile", Icons.Default.Person, Color(0xFF2196F3))
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1A1A2E)
            ) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = item.selectedColor,
                            selectedTextColor = item.selectedColor,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = item.selectedColor.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        when (selectedTabIndex) {
            0 -> NutriHomeScreen(
                viewModel = nutriViewModel,
                onAddClick = onAddFoodClick,
                modifier = Modifier.padding(innerPadding)
            )
            1 -> SummaryScreen(
                state = sleepState,
                onStart = onStartSleepTracking,
                onViewHistory = onViewSleepHistory,
                modifier = Modifier.padding(innerPadding)
            )
            2 -> DashboardScreen(
                stats = stats,
                progress = progress,
                workouts = workouts,
                logs = logs,
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                onWorkoutClick = onWorkoutClick,
                onDeleteLog = onDeleteLog,
                onDeleteAllLogs = onDeleteAllLogs,
                modifier = Modifier.padding(innerPadding)
            )
            3 -> ProfileScreen(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}


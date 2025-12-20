package com.example.fitalog.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.fitalog.data.WorkoutRepository
import com.example.fitalog.data.SleepApi
import com.example.fitalog.data.toDomain
import com.example.fitalog.data.toDto
import com.example.fitalog.model.SleepSession
import com.example.fitalog.model.SleepSyncState
import com.example.fitalog.model.WeeklyDataPoint
import com.example.fitalog.ui.screen.workit.DashboardScreen
import com.example.fitalog.ui.screen.workit.WorkoutSessionScreen
import com.example.fitalog.ui.screen.MainScreen
import com.example.fitalog.ui.screen.sleepsync.SleepHistoryScreen
import com.example.fitalog.ui.screen.sleepsync.SummaryScreen
import com.example.fitalog.ui.screen.sleepsync.TrackingActiveScreen
import com.example.fitalog.ui.screen.sleepsync.TrackingCompleteScreen
import com.example.fitalog.ui.screen.nutrinote.NutriHomeScreen
import com.example.fitalog.ui.screen.nutrinote.AddFoodScreen
import com.example.fitalog.ui.screen.nutrinote.NutriViewModel
import com.example.fitalog.viewmodel.WorkoutViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.random.Random
import kotlin.ranges.coerceAtLeast


fun calculateSleepScore(durationMinutes: Long): Int {
    val h = durationMinutes / 60f

    return when {
        h < 1f  -> 5    // < 1 jam  = sangat buruk
        h < 3f  -> 20   // 1–3 jam  = buruk
        h < 5f  -> 40   // 3–5 jam  = kurang
        h < 7f  -> 60   // 5–7 jam  = lumayan
        h < 9f  -> 85   // 7–9 jam  = ideal
        h < 11f -> 70   // 9–11 jam = kebanyakan
        else    -> 50   // > 11 jam = tidak ideal
    }
}

fun buildWeeklyData(sessions: List<SleepSession>): List<WeeklyDataPoint> {
    if (sessions.isEmpty()) return emptyList()

    val now: LocalDate = LocalDate.now()
    val sevenDaysAgo = now.minusDays(6)
    val dayFormatter = DateTimeFormatter.ofPattern("E")

    val filtered = sessions.filter { session ->
        val date = session.startTime.toLocalDate()
        session.endTime != null &&
                (date.isAfter(sevenDaysAgo.minusDays(1)) && date.isBefore(now.plusDays(1)))
    }

    val grouped = filtered.groupBy { it.startTime.format(dayFormatter) }

    val dayOrder = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    return dayOrder.mapNotNull { day ->
        val list = grouped[day] ?: return@mapNotNull null
        val totalMinutes = list.sumOf { s ->
            val end = s.endTime!!
            ChronoUnit.MINUTES.between(s.startTime, end)
        }
        WeeklyDataPoint(day = day, durationHours = totalMinutes / 60f)
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember(context) { WorkoutRepository(context) }
    val workoutViewModel: WorkoutViewModel = viewModel(
        factory = WorkoutViewModel.provideFactory(repository)
    )

    val nutriViewModel: NutriViewModel = viewModel()

    val stats by workoutViewModel.todayStats.collectAsState()
    val weeklyProgress by workoutViewModel.weeklyProgress.collectAsState()
    val logs by workoutViewModel.logs.collectAsState()
    val timerState by workoutViewModel.timerState.collectAsState()
    val isRefreshing by workoutViewModel.isRefreshing.collectAsState()


    var sleepState by remember { mutableStateOf(SleepSyncState()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            sleepState = sleepState.copy(isLoading = true, errorMessage = null)

            val remoteSessions = SleepApi.service.getSleepSessions()
            val mapped = remoteSessions.map { it.toDomain() }

            val weekly = buildWeeklyData(mapped)
            val lastScore = mapped.firstOrNull()?.score ?: sleepState.sleepScore

            sleepState = sleepState.copy(
                isLoading = false,
                recentSessions = mapped,
                sleepScore = lastScore,
                weeklyData = weekly
            )
        } catch (e: Exception) {
            sleepState = sleepState.copy(
                isLoading = false,
                errorMessage = e.message ?: "Gagal memuat data dari server"
            )
        }
    }

    val startSleepTracking: () -> Unit = {
        sleepState = sleepState.copy(
            isTracking = true,
            sessionStartTime = LocalDateTime.now()
        )
        navController.navigate(ScreenSleep.Active.route) {
            popUpTo(ScreenSleep.Summary.route) { inclusive = false }
        }
    }

    val refreshSleepData: () -> Unit = {
        coroutineScope.launch {
            try {
                println("🔄 Memuat ulang data tidur dari Supabase...")
                sleepState = sleepState.copy(isLoading = true, errorMessage = null)

                val remoteSessions = SleepApi.service.getSleepSessions()
                val mapped = remoteSessions.map { it.toDomain() }

                val weekly = buildWeeklyData(mapped)
                val lastScore = mapped.firstOrNull()?.score ?: sleepState.sleepScore

                sleepState = sleepState.copy(
                    isLoading = false,
                    recentSessions = mapped,
                    sleepScore = lastScore,
                    weeklyData = weekly,
                    errorMessage = null
                )

                println("✅ Data berhasil dimuat ulang: ${mapped.size} sesi")
            } catch (e: Exception) {
                println("❌ Error memuat ulang data: ${e.message}")
                sleepState = sleepState.copy(
                    isLoading = false,
                    errorMessage = "Gagal memuat data: ${e.message}"
                )
            }
        }
    }

    val endSleepSession: () -> Unit = {
        val endTime = LocalDateTime.now()
        val startTime = sleepState.sessionStartTime

        if (startTime != null) {
            val durationMinutes = ChronoUnit.MINUTES.between(startTime, endTime)
            val hours = durationMinutes / 60
            val minutes = durationMinutes % 60
            val durationString = "${hours}h ${minutes}m"

            val deepMinutes = (durationMinutes * 0.25).toLong() + Random.nextInt(-10, 10)
            val remMinutes = (durationMinutes * 0.20).toLong() + Random.nextInt(-5, 5)
            val awakeMinutes = Random.nextInt(5, 15)
            val lightMinutes = (durationMinutes - deepMinutes - remMinutes - awakeMinutes)
                .coerceAtLeast(0)

            val formatMinutes = { m: Long -> "${m / 60}h ${m % 60}m" }
            val newScore = calculateSleepScore(durationMinutes)

            val newSession = SleepSession(
                startTime = startTime,
                endTime = endTime,
                duration = durationString,
                score = newScore,
                deepSleep = formatMinutes(deepMinutes),
                remSleep = formatMinutes(remMinutes),
                lightSleep = formatMinutes(lightMinutes),
                awakeTime = "${awakeMinutes}m",
                date = startTime.format(DateTimeFormatter.ofPattern("dd MMM")),
                day = startTime.format(DateTimeFormatter.ofPattern("E"))
            )

            val newRecentSessions = listOf(newSession) + sleepState.recentSessions

            coroutineScope.launch {
                try {
                    println("🔄 Menyimpan sesi tidur ke Supabase...")
                    val dto = newSession.toDto(userId = null)
                    val savedSessionList = SleepApi.service.insertSleepSession(dto)
                    val savedSession = savedSessionList.firstOrNull()

                    if (savedSession != null) {
                        println("✅ Berhasil menyimpan ke Supabase: ${savedSession.id}")
                        val savedDomain = savedSession.toDomain()
                        val updatedSessions = listOf(savedDomain) + sleepState.recentSessions

                        sleepState = sleepState.copy(
                            isTracking = false,
                            sessionStartTime = null,
                            sleepScore = newScore,
                            weeklyData = buildWeeklyData(updatedSessions),
                            recentSessions = updatedSessions,
                            lastCompletedSession = savedDomain,
                            errorMessage = null
                        )
                        println("✅ State berhasil diupdate dengan ${updatedSessions.size} sesi")
                    } else {
                        throw Exception("Server tidak mengembalikan data yang disimpan")
                    }
                } catch (e: Exception) {
                    println("❌ Error menyimpan ke Supabase: ${e.message}")
                    e.printStackTrace()

                    sleepState = sleepState.copy(
                        isTracking = false,
                        sessionStartTime = null,
                        sleepScore = newScore,
                        weeklyData = buildWeeklyData(newRecentSessions),
                        recentSessions = newRecentSessions,
                        lastCompletedSession = newSession,
                        errorMessage = "Data tersimpan lokal. Gagal sinkronisasi: ${e.message}"
                    )
                }

                navController.navigate(ScreenSleep.Complete.route) {
                    popUpTo(ScreenSleep.Summary.route) { inclusive = false }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route,
        modifier = modifier
    ) {
        composable(route = Screen.Main.route) {
            MainScreen(
                nutriViewModel = nutriViewModel,
                onAddFoodClick = { meal ->
                    navController.navigate(ScreenNutri.AddFood.createRoute(meal))
                },
                sleepState = sleepState,
                onStartSleepTracking = startSleepTracking,
                onViewSleepHistory = { navController.navigate(ScreenSleep.History.route) },
                stats = stats,
                progress = weeklyProgress,
                workouts = workoutViewModel.workouts,
                logs = logs,
                isRefreshing = isRefreshing,
                onRefresh = workoutViewModel::refreshLogs,
                onWorkoutClick = { workoutId ->
                    workoutViewModel.startSession(workoutId)
                    navController.navigate(Screen.Session.createRoute(workoutId))
                },
                onDeleteLog = workoutViewModel::deleteLog,
                onDeleteAllLogs = workoutViewModel::clearLogs
            )
        }

        composable(route = ScreenSleep.Summary.route) {
            SummaryScreen(
                state = sleepState,
                onStart = startSleepTracking,
                onViewHistory = { navController.navigate(ScreenSleep.History.route) }
            )
        }

        composable(route = ScreenSleep.Active.route) {
            TrackingActiveScreen(
                state = sleepState,
                onStop = endSleepSession,
                onBack = { /* user harus stop via tombol */ }
            )
        }


        composable(route = ScreenNutri.Home.route) {
            NutriHomeScreen(
                viewModel = nutriViewModel,
                onAddClick = { meal ->
                    navController.navigate(ScreenNutri.AddFood.createRoute(meal))
                }
            )
        }

        composable(
            route = ScreenNutri.AddFood.route,
            arguments = listOf(
                navArgument("meal") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val meal = backStackEntry.arguments?.getString("meal") ?: "Sarapan"
            AddFoodScreen(
                initialMeal = meal,
                viewModel = nutriViewModel,
                onFoodAdded = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(route = ScreenSleep.Complete.route) {
            val session = sleepState.lastCompletedSession ?: sleepState.recentSessions.firstOrNull()
            if (session != null) {
                TrackingCompleteScreen(
                    session = session,
                    onBackToSummary = {
                        navController.navigate(ScreenSleep.Summary.route) {
                            popUpTo(ScreenSleep.Summary.route) { inclusive = true }
                        }
                    }
                )
            } else {
                navController.navigate(ScreenSleep.Summary.route) {
                    popUpTo(ScreenSleep.Summary.route) { inclusive = true }
                }
            }
        }


        composable(route = ScreenSleep.History.route) {
            SleepHistoryScreen(
                sessions = sleepState.recentSessions,
                onBack = { navController.navigateUp() },
                onRefresh = refreshSleepData,
                isRefreshing = sleepState.isLoading
            )
        }


        composable(route = Screen.Dashboard.route) {
            DashboardScreen(
                stats = stats,
                progress = weeklyProgress,
                workouts = workoutViewModel.workouts,
                logs = logs,
                isRefreshing = isRefreshing,
                onRefresh = workoutViewModel::refreshLogs,
                onWorkoutClick = { workoutId ->
                    workoutViewModel.startSession(workoutId)
                    navController.navigate(Screen.Session.createRoute(workoutId))
                },
                onDeleteLog = workoutViewModel::deleteLog,
                onDeleteAllLogs = workoutViewModel::clearLogs
            )
        }

        composable(
            route = Screen.Session.route,
            arguments = listOf(
                navArgument("workoutId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getInt("workoutId")
            val workout = workoutId?.let { workoutViewModel.getWorkoutById(it) }

            WorkoutSessionScreen(
                workout = workout,
                timerState = timerState,
                onStart = workoutViewModel::startTimer,
                onPause = workoutViewModel::pauseTimer,
                onResume = workoutViewModel::resumeTimer,
                onFinish = { uri -> workoutViewModel.finishWorkout(uri) },
                onBack = {
                    workoutViewModel.cancelSession()
                    navController.navigateUp()
                }
            )
        }
    }
}

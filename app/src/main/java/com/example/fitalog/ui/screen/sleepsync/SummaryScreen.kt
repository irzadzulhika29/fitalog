package com.example.fitalog.ui.screen.sleepsync

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitalog.model.SleepSession
import com.example.fitalog.model.SleepSyncState
import com.example.fitalog.model.WeeklyDataPoint
import com.example.fitalog.ui.theme.AccentGreen
import com.example.fitalog.ui.theme.CardBackground
import com.example.fitalog.ui.theme.ChartBarColor
import com.example.fitalog.ui.theme.DarkBackground
import com.example.fitalog.ui.theme.PoorSleep
import com.example.fitalog.ui.theme.ScoreCardGradientEnd
import com.example.fitalog.ui.theme.ScoreCardGradientStart
import com.example.fitalog.ui.theme.StatusGood
import com.example.fitalog.ui.theme.StatusNormal
import com.example.fitalog.ui.theme.TextPrimary
import com.example.fitalog.ui.theme.TextSecondary
import com.example.fitalog.ui.theme.SleepSyncTheme
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

// =====================
// Util: status warna & teks
// =====================

fun getStatusColor(score: Int): Color =
    when {
        score >= 80 -> StatusGood
        score >= 60 -> StatusNormal
        else -> PoorSleep
    }

fun getStatusText(score: Int): String =
    when {
        score >= 90 -> "Excellent Sleep"
        score >= 80 -> "Great Sleep"
        score >= 70 -> "Good Sleep"
        score >= 60 -> "Fair Sleep"
        else -> "Poor Sleep"
    }

// Total jam mingguan untuk title besar (mis. "42h 55m")
fun calculateWeeklyHours(weeklyData: List<WeeklyDataPoint>): String {
    val totalHours = weeklyData.sumOf { it.durationHours.toDouble() }.toFloat()
    val hours = totalHours.toInt()
    val minutes = ((totalHours - hours) * 60).roundToInt()
    return "${hours}h ${minutes}m"
}

// =====================
// SUMMARY SCREEN
// =====================

@Composable
fun SummaryScreen(
    state: SleepSyncState,
    onStart: () -> Unit,
    onViewHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val score = state.sleepScore
    val statusColor = getStatusColor(score)
    val statusText = getStatusText(score)
    val weeklyHours = calculateWeeklyHours(state.weeklyData)
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        AppHeader(title = "Sleepsync Overview")

        // Tampilkan error message jika ada
        state.errorMessage?.let { errorMsg ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFF5252).copy(alpha = 0.2f))
                    .padding(12.dp)
            ) {
                Text(
                    text = "⚠️ $errorMsg",
                    color = Color(0xFFFF5252),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Konten utama dibuat scrollable
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Sleep Score Card
            SleepScoreCard(
                score = score,
                status = statusText,
                color = statusColor
            )

            Spacer(Modifier.height(16.dp))

            // 2. Weekly Sleep Card (grafik dari weeklyData)
            WeeklyHoursCard(
                totalWeeklyHours = weeklyHours,
                weeklyData = state.weeklyData
            )

            Spacer(Modifier.height(16.dp))

            // 3. Recent Sessions (list JSON, + tombol "View All")
            RecentSessionsCard(
                sessions = state.recentSessions,
                onViewHistory = onViewHistory
            )

            Spacer(Modifier.height(16.dp))
        }

        // Tombol utama di bawah
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            PrimaryActionButton(
                text = if (state.isTracking) "VIEW ACTIVE TRACKING" else "START SLEEP",
                onClick = onStart
            )
        }
    }
}

// =====================
// Komponen-komponen Summary
// =====================

@Composable
fun SleepScoreCard(score: Int, status: String, color: Color) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = CardBackground),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "LAST NIGHT SCORE",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Donut score
                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 12.dp.toPx()
                        val diameter = size.minDimension
                        val topLeft = (size.minDimension - diameter) / 2f

                        // background arc
                        drawArc(
                            color = CardBackground.copy(alpha = 0.6f),
                            startAngle = 135f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                            size = Size(diameter, diameter),
                            topLeft = androidx.compose.ui.geometry.Offset(topLeft, topLeft)
                        )

                        // progress arc dengan gradient
                        val sweep = 270f * (score / 100f)
                        drawArc(
                            brush = Brush.linearGradient(
                                listOf(ScoreCardGradientStart, ScoreCardGradientEnd)
                            ),
                            startAngle = 135f,
                            sweepAngle = sweep,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                            size = Size(diameter, diameter),
                            topLeft = androidx.compose.ui.geometry.Offset(topLeft, topLeft)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = score.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "/100",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(Modifier.width(16.dp))

                Column {
                    Text(
                        text = status,
                        style = MaterialTheme.typography.titleMedium,
                        color = color,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Based on duration & depth",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyHoursCard(
    totalWeeklyHours: String,
    weeklyData: List<WeeklyDataPoint>
) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = CardBackground),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "WEEKLY SLEEP",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = totalWeeklyHours,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(Modifier.height(16.dp))

            val dayOrder = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            val sortedData = weeklyData.sortedBy { dayOrder.indexOf(it.day) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val idealMax = 9f // skala visual maksimum 9 jam
                sortedData.forEach { point ->
                    val fraction = (point.durationHours / idealMax).coerceIn(0f, 1f)
                    val barHeight = (fraction * 100).dp

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .height(barHeight)
                                .width(10.dp)
                                .background(
                                    color = ChartBarColor,
                                    shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                                )
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = point.day,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentSessionsCard(
    sessions: List<SleepSession>,
    onViewHistory: () -> Unit
) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = CardBackground),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT SESSIONS",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Row(
                    modifier = Modifier
                        .clickable { onViewHistory() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentGreen
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "View All",
                        tint = AccentGreen
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

            sessions.take(3).forEachIndexed { index, session ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon kalender
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = CardBackground,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Date",
                            tint = AccentGreen
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${session.day}, ${session.date}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        val startText = session.startTime.format(timeFormatter)
                        val endText = session.endTime?.format(timeFormatter) ?: "-"
                        Text(
                            text = "$startText - $endText",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    Text(
                        text = session.duration,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AccentGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (index != sessions.take(3).lastIndex) {
                    CustomDivider(
                        color = TextSecondary.copy(alpha = 0.2f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

// =====================
// Preview
// =====================

@Preview(showBackground = true)
@Composable
private fun PreviewSummaryScreen() {
    val mockState = SleepSyncState(
        sleepScore = 58,
        weeklyData = listOf(
            WeeklyDataPoint("Mon", 6.5f),
            WeeklyDataPoint("Tue", 7.8f),
            WeeklyDataPoint("Wed", 7.2f),
            WeeklyDataPoint("Thu", 5.3f),
            WeeklyDataPoint("Fri", 8.0f),
            WeeklyDataPoint("Sat", 1.0f),
            WeeklyDataPoint("Sun", 7.0f)
        ),
        recentSessions = emptyList()
    )
    SleepSyncTheme {
        SummaryScreen(
            state = mockState,
            onStart = {},
            onViewHistory = {}
        )
    }
}

package com.example.fitalog.ui.screen.sleepsync

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitalog.model.SleepSession
import com.example.fitalog.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Data Model untuk Report di AppNavHost sudah digunakan: SleepSession

@Composable
fun TrackingCompleteScreen(
    session: SleepSession, // Menerima data sesi yang baru selesai
    onBackToSummary: () -> Unit
) {
    val statusColor = getStatusColor(session.score)
    val statusText = getStatusText(session.score)
    val goHomeTime = session.startTime.format(DateTimeFormatter.ofPattern("h:mm a"))
    val wakeTime = session.endTime?.format(DateTimeFormatter.ofPattern("h:mm a")) ?: "N/A"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        AppHeader(title = "Sleep Complete")

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Status Card (Good Morning!)
            StatusCard(statusText, statusColor)

            Spacer(Modifier.height(16.dp))

            // 2. Time Card (Row 1: Go to Bed & Wake Time)
            TimeCard(goHomeTime, wakeTime)

            Spacer(Modifier.height(16.dp))

            // 3. Duration Card (Row 2: Total & Deep Sleep)
            DurationCard(session.duration, session.deepSleep)

            Spacer(Modifier.height(16.dp))

            // 4. Sleep Details (Remaining Metrics)
            SleepDetailsCard(session)

            Spacer(Modifier.height(16.dp))
        }

        // 5. Action Button (Bottom)
        Box(modifier = Modifier.padding(16.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
            TextButton(onClick = onBackToSummary) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextSecondary)
                    Spacer(Modifier.width(4.dp))
                    Text(text = "Back to Summary", color = TextSecondary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- Komponen-komponen spesifik TrackingCompleteScreen (Menggunakan data real) ---

@Composable
@Suppress("UNUSED_PARAMETER")
fun StatusCard(statusText: String, statusColor: Color) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = CardBackground),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = "Good Morning!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
    }
}

@Composable
fun TimeCard(goHomeTime: String, wakeTime: String) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = CardBackground),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MetricItem(
                icon = Icons.Default.NightsStay,
                label = "GO TO BED",
                value = goHomeTime,
                color = TextSecondary
            )
            CustomDivider(color = DarkBackground, thickness = 1.dp, modifier = Modifier.height(60.dp).width(1.dp).background(TextSecondary.copy(alpha = 0.1f)))
            MetricItem(
                icon = Icons.Default.WbSunny,
                label = "WAKE TIME",
                value = wakeTime,
                color = AccentGreen // Menggunakan AccentGreen sebagai warna utama
            )
        }
    }
}

@Composable
fun DurationCard(totalDuration: String, deepSleep: String) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = CardBackground),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MetricItem(
                icon = Icons.Default.Alarm,
                label = "TOTAL DURATION",
                value = totalDuration,
                color = AccentGreen
            )
            CustomDivider(color = DarkBackground, thickness = 1.dp, modifier = Modifier.height(60.dp).width(1.dp).background(TextSecondary.copy(alpha = 0.1f)))
            MetricItem(
                icon = Icons.Default.Bed,
                label = "DEEP SLEEP",
                value = deepSleep,
                color = AccentBlue // Menggunakan AccentBlue untuk Deep Sleep
            )
        }
    }
}

@Composable
fun SleepDetailsCard(session: SleepSession) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = CardBackground),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "SLEEP DETAILS", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(Modifier.height(8.dp))

            // Fungsi helper untuk baris detail
            @Composable
            fun DetailRow(label: String, value: String) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }

            DetailRow("REM Sleep", session.remSleep)
            CustomDivider(color = TextSecondary.copy(alpha = 0.1f), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
            DetailRow("Light Sleep", session.lightSleep)
            CustomDivider(color = TextSecondary.copy(alpha = 0.1f), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
            DetailRow("Awake Time", session.awakeTime)
        }
    }
}

@Preview
@Composable
private fun PreviewTrackingCompleteScreen() {
    val mockSession = SleepSession(
        startTime = LocalDateTime.now().minusHours(8).minusMinutes(15),
        endTime = LocalDateTime.now(),
        duration = "8h 15m",
        score = 87,
        deepSleep = "2h 15m",
        remSleep = "1h 45m",
        lightSleep = "4h 0m",
        awakeTime = "15m",
        date = "01 Nov",
        day = "Wed"
    )
    SleepSyncTheme {
        TrackingCompleteScreen(
            session = mockSession,
            onBackToSummary = {}
        )
    }
}
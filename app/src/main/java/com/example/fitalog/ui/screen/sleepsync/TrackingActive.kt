package com.example.fitalog.ui.screen.sleepsync

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitalog.model.SleepSyncState
import com.example.fitalog.ui.theme.AccentGreen
import com.example.fitalog.ui.theme.DarkBackground
import com.example.fitalog.ui.theme.SleepSyncTheme
import com.example.fitalog.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Fungsi utilitas untuk menghitung dan memformat durasi (HH:MM:SS)
private fun formatDuration(duration: Duration): String {
    val totalSeconds = duration.seconds
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

@Composable
fun TrackingActiveScreen(
    state: SleepSyncState,
    onStop: () -> Unit,
    onBack: () -> Unit // Ignored, user must stop tracking first
) {
    // State lokal untuk menghitung durasi secara real-time
    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }

    // Efek untuk memperbarui waktu setiap detik (untuk timer)
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = LocalDateTime.now()
        }
    }

    // Hitung durasi
    val duration = remember(currentTime, state.sessionStartTime) {
        if (state.sessionStartTime != null) {
            Duration.between(state.sessionStartTime, currentTime)
        } else {
            Duration.ZERO
        }
    }

    val formattedDuration = formatDuration(duration)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppHeader(title = "Sleep Tracking Active")

        Spacer(Modifier.height(64.dp))

        // 1. Visual Pelacakan (Mockup: Bulan dan Lingkaran)
        TrackingVisual()

        Spacer(Modifier.height(48.dp))

        // 2. Timer Durasi
        Text(
            text = formattedDuration,
            fontSize = 56.sp,
            fontWeight = FontWeight.Light,
            color = Color.White
        )
        Text(
            text = "Deep Tracking in progress",
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary
        )

        Spacer(Modifier.height(16.dp))

        // 3. Waktu Mulai Tidur (Go to Bed Time)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.Bedtime, contentDescription = "Start Time", tint = AccentGreen)
            Spacer(Modifier.width(8.dp))
            // Format waktu menggunakan LocalDateTime
            Text(
                text = "Started at: ${state.sessionStartTime?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: "N/A"}",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
        }

        Spacer(Modifier.height(64.dp))

        // 4. Tombol Aksi (Stop Sleeping)
        PrimaryActionButton(
            text = "STOP SLEEPING",
            onClick = onStop,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 5. Tombol Kembali ke Summary (Hanya placeholder, disarankan agar user menekan STOP)
        TextButton(onClick = onStop) { // Diarahkan ke onStop agar tidak mengganggu state
            Text(text = "Back to Summary", color = TextSecondary)
        }
    }
}

// Komponen visual bulan dan lingkaran (Mockup)
@Composable
fun TrackingVisual() {
    Box(
        modifier = Modifier
            .size(180.dp)
            .background(Color(0xFF1F222F), shape = androidx.compose.foundation.shape.CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Kita gunakan ikon bulan/bintang sederhana sebagai visual
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Tracking Active",
            tint = AccentGreen.copy(alpha = 0.8f),
            modifier = Modifier.size(80.dp)
        )
    }
}

@Preview
@Composable
private fun PreviewTrackingActiveScreen() {
    // Mock State
    val mockState = SleepSyncState(
        isTracking = true,
        sessionStartTime = LocalDateTime.now().minusMinutes(127).minusSeconds(23)
    )
    SleepSyncTheme {
        TrackingActiveScreen(
            state = mockState,
            onStop = {},
            onBack = {}
        )
    }
}
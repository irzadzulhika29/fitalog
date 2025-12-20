package com.example.fitalog.ui.screen.sleepsync

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitalog.ui.theme.AccentGreen
import com.example.fitalog.ui.theme.DarkBackground
import com.example.fitalog.ui.theme.SleepSyncTheme
import com.example.fitalog.ui.theme.TextSecondary

@Composable
fun TrackingNonActiveScreen(
    onStartTracking: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppHeader(title = "Sleep Tracking Non Active")

        Spacer(Modifier.height(80.dp))

        // Visual "Put Your Mind to Bed" (Mockup)
        Box(
            modifier = Modifier
                .size(240.dp)
                .background(Color(0xFF1F222F), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder text for the visual
            Text(
                text = "PUT YOUR MIND TO BED",
                color = AccentGreen,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(32.dp)
            )
        }

        Spacer(Modifier.height(64.dp))

        Text(
            text = "Are you ready to sleep?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Make sure your device is placed safely and connected to power.",
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(Modifier.weight(1f)) // Dorong tombol ke bawah

        // Tombol Aksi: Mulai (mengarah ke TrackingActive)
        PrimaryActionButton(
            text = "GO TO BED",
            onClick = onStartTracking,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Tombol Kembali
        TextButton(onClick = onBack) {
            Text(text = "Back to Summary", color = TextSecondary)
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Preview
@Composable
private fun PreviewTrackingNonActiveScreen() {
    SleepSyncTheme {
        TrackingNonActiveScreen(
            onStartTracking = {},
            onBack = {}
        )
    }
}
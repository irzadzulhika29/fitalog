package com.example.fitalog.ui.screen.sleepsync

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.fitalog.model.SleepSession
import com.example.fitalog.ui.theme.*
import java.time.format.DateTimeFormatter


// --- Implementasi Layout Sleep History ---

@Composable
fun SleepHistoryScreen(
    sessions: List<SleepSession>, // Langsung terima list tanpa 'initial'
    onBack: () -> Unit,
    onRefresh: () -> Unit, // Callback untuk refresh data
    isRefreshing: Boolean = false // Status loading
) {

    // State untuk mengelola SnackBar
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Fungsi untuk menampilkan SnackBar
    val showSnackbar: (SleepSession) -> Unit = { session ->
        // Format waktu tidur untuk ditampilkan di SnackBar
        val timeRange = if (session.endTime != null) {
            val start = session.startTime.format(DateTimeFormatter.ofPattern("h:mm a"))
            val end = session.endTime.format(DateTimeFormatter.ofPattern("h:mm a"))
            "$start - $end"
        } else {
            "N/A"
        }

        scope.launch {
            // MENAMBAH DETAIL: Durasi dan Waktu Tidur ditambahkan ke pesan.
            snackbarHostState.showSnackbar(
                message = "${session.day}, ${session.date} | Durasi: ${session.duration} | Skor: ${session.score}\n$timeRange",
                actionLabel = "TUTUP",
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.background(DarkBackground)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(paddingValues)
        ) {
            // Header dengan Tombol Back
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Sleep History", // Judul: Riwayat Tidur
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                }

                // Tombol Refresh
                IconButton(
                    onClick = onRefresh,
                    enabled = !isRefreshing
                ) {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = AccentGreen,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Data",
                            tint = AccentGreen
                        )
                    }
                }
            }

            // --- LAZYCOLUMN: Menampung daftar item ---
            if (sessions.isEmpty()) {
                Text(
                    text = "Belum ada riwayat tidur yang tersimpan.",
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp) // Jarak antar item
                ) {
                    // items digunakan untuk memuat semua sesi hanya dengan satu jenis layout
                    items(sessions, key = { it.id }) { session ->
                        // Hanya menggunakan Layout 1: Sesi Tidur Standar
                        HistorySessionItem(session = session, onSessionClick = showSnackbar)
                    }
                }
            }
        }
    }
}

// --- Item View 1: HistorySessionItem (Standar) ---

@Composable
fun HistorySessionItem(session: SleepSession, onSessionClick: (SleepSession) -> Unit) {
    // Logika penghitungan waktu
    val timeRange = if (session.endTime != null) {
        val start = session.startTime.format(DateTimeFormatter.ofPattern("h:mm a"))
        val end = session.endTime.format(DateTimeFormatter.ofPattern("h:mm a"))
        "$start - $end"
    } else {
        "N/A"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSessionClick(session) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "${session.day}, ${session.date}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = timeRange,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
            // Durasi dan Skor
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = session.duration,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AccentGreen
                )
                Text(
                    text = "Score: ${session.score}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}



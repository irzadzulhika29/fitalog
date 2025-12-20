package com.example.fitalog.model

import java.time.LocalDateTime
import java.util.UUID

data class SleepSession(
    val id: String = UUID.randomUUID().toString(),
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?,
    val duration: String,
    val score: Int,
    val deepSleep: String = "0h 0m",
    val remSleep: String = "0h 0m",
    val lightSleep: String = "0h 0m",
    val awakeTime: String = "0m",
    val date: String,
    val day: String
)

data class WeeklyDataPoint(
    val day: String,
    val durationHours: Float
)

data class SleepSyncState(
    val isTracking: Boolean = false,
    val sleepScore: Int = 0,
    val sessionStartTime: LocalDateTime? = null,
    val lastCompletedSession: SleepSession? = null,
    val weeklyData: List<WeeklyDataPoint> = emptyList(),
    val recentSessions: List<SleepSession> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)


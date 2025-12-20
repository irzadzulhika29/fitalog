

package com.example.fitalog.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.*

// --- Primary Colors & Neon Accents ---
val Primary = Color(0xFF27E1C1)
val PrimaryLight = Color(0xFF3DF5D7)
val PrimaryDark = Color(0xFF1EB89A)

// --- Backgrounds & Surfaces ---
val Bg = Color(0xFF0E1117)
val BgCard = Color(0xFF151A22)
val BgDark = Color(0xFF0A0D12)
val DarkBackground = Color(0xFF0F121A)
val CardBackground = Color(0xFF1C2333)

// --- Text Colors ---
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF9BA3B4)
val TextMuted = Color(0xFF6C7280)

// --- Accent Colors ---
val AccentPurple = Color(0xFF8A51F2)
val AccentBlue = Color(0xFF51A5F2)
val AccentGreen = Color(0xFF51F2C2) // Primary action color
val AccentRed = Color(0xFFF25151)

// --- Status Colors ---
val Success = Color(0xFF27E1A2)
val Warning = Color(0xFFFFC857)
val Danger = Color(0xFFFF5C8A)
val StatusGood = Color(0xFF90EE90) // Light Green for good score
val StatusNormal = Color(0xFFADD8E6) // Light Blue for normal score
val PoorSleep = Color(0xFFF08080) // Light Red for poor score

// --- Score Card Gradient (used in SummaryScreen) ---
val ScoreCardGradientStart = Color(0xFF8A51F2) // Purple
val ScoreCardGradientEnd = Color(0xFF51F2C2) // Green

// --- Bar Chart Color ---
val ChartBarColor = Color(0xFF51F2C2)

val BG_MAIN = Color(0xFF0F121A)
val TOP_ACCENT = Color(0xFF51F2C2)
val CARD_BG = Color(0xFF1C2333)
val TRACK_GRAY = Color(0xFFF2F2F2)
val WHITE = Color(0xFFFFFFFF)
val ACCENT = Color(0xFF51F2C2)
val TEXT_MUTED = Color(0xFF9AA0A6)

private val DarkColorScheme = darkColorScheme(
    primary = ACCENT,
    background = BG_MAIN,
    surface = CARD_BG,
    onPrimary = BG_MAIN,
    onBackground = WHITE,
    onSurface = WHITE
)

@Composable
fun FitaLogTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content
    )
}
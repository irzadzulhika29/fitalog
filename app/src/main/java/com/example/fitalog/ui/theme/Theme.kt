

package com.example.fitalog.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// --- Dark Color Scheme (Primary) ---
private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = BgDark,
    primaryContainer = BgCard,
    onPrimaryContainer = TextPrimary,
    secondary = PrimaryLight,
    onSecondary = BgDark,
    secondaryContainer = BgCard,
    onSecondaryContainer = TextPrimary,
    tertiary = PrimaryDark,
    onTertiary = BgDark,
    background = Bg,
    onBackground = TextPrimary,
    surface = Bg,
    onSurface = TextPrimary,
    surfaceVariant = BgCard,
    onSurfaceVariant = TextSecondary,
    outline = Primary.copy(alpha = 0.35f)
)

// --- Light Color Scheme (Alternative) ---
private val LightColorScheme = lightColorScheme(
    primary = AccentPurple,
    secondary = AccentBlue,
    tertiary = AccentGreen,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    surfaceVariant = Color.LightGray,
    error = AccentRed,
    onError = Color.White
)

// --- Sleep Sync Dark Color Scheme ---
private val SleepSyncDarkColorScheme = darkColorScheme(
    primary = AccentPurple,
    secondary = AccentBlue,
    tertiary = AccentGreen,
    background = DarkBackground,
    surface = CardBackground,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = TextSecondary,
    error = AccentRed,
    onError = Color.White
)

// --- Main App Theme ---
@Composable
fun PAMProjectTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// --- Sleep Sync Theme (for sleep feature) ---
@Composable
fun SleepSyncTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> SleepSyncDarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
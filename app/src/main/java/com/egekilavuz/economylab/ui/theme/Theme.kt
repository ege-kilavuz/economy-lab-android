package com.egekilavuz.economylab.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF60A5FA),
    onPrimary = Color.White,
    surface = Color(0xFF0F172A),
    onSurface = Color.White,
    background = Color(0xFF0B1220),
    onBackground = Color.White,
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155),
    error = Color(0xFFEF4444),
    onError = Color.White,
    secondary = Color(0xFF22C55E),
    onSecondary = Color.White,
    tertiary = Color(0xFFF59E0B),
    onTertiary = Color.White,
)

@Composable
fun EconomyLabTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = content
    )
}

package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = TextPrimary,
    secondary = TextSecondary,
    onSecondary = CardSurface,
    background = BackgroundDeep,
    onBackground = TextPrimary,
    surface = CardSurface,
    onSurface = TextPrimary,
    error = DangerRed,
    onError = TextPrimary,
    outline = TextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = AccentBlue,
    onPrimary = TextPrimary,
    secondary = TextSecondary,
    onSecondary = CardSurface,
    background = BackgroundDeep, // The user requested premium dark theme ALWAYS by default
    onBackground = TextPrimary,
    surface = CardSurface,
    onSurface = TextPrimary,
    error = DangerRed,
    onError = TextPrimary,
    outline = TextSecondary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark theme by default for Premium Dark branding
    dynamicColor: Boolean = false, // Disable dynamic colors to ensure our carefully hand-crafted premium theme colors are rendered perfectly
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BackgroundDeep.toArgb()
            window.navigationBarColor = BackgroundDeep.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

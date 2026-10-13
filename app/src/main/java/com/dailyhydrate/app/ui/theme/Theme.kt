package com.dailyhydrate.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = DeepAqua,
    onPrimary = Color.White,
    primaryContainer = SoftBlue,
    onPrimaryContainer = InkDark,
    secondary = TealMint,
    onSecondary = Color.White,
    secondaryContainer = SoftTeal,
    onSecondaryContainer = InkDark,
    tertiary = AquaSky,
    onTertiary = Color.White,
    background = SurfaceTint,
    onBackground = InkDark,
    surface = CardWhite,
    onSurface = InkDark,
    surfaceVariant = SoftBlue,
    onSurfaceVariant = InkMuted,
    outline = Color(0xFFB9D6E2),
    error = WarningRed,
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = AquaSky,
    onPrimary = Color(0xFF00263A),
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = OnDarkInk,
    secondary = TealMint,
    onSecondary = Color(0xFF00263A),
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = OnDarkInk,
    tertiary = AquaSky,
    onTertiary = Color(0xFF00263A),
    background = DarkBackground,
    onBackground = OnDarkInk,
    surface = DarkSurface,
    onSurface = OnDarkInk,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = OnDarkMuted,
    outline = Color(0xFF2C4C5C),
    error = WarningRed,
    onError = Color.White
)

@Composable
fun DailyHydrateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

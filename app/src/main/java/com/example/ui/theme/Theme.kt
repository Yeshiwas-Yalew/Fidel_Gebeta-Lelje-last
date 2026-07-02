package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = DarkKidPrimary,
    secondary = DarkKidSecondary,
    tertiary = DarkKidTertiary,
    background = DarkKidBackground
  )

private val LightColorScheme =
  lightColorScheme(
    primary = KidPrimary,
    secondary = KidSecondary,
    tertiary = KidTertiary,
    background = KidBackground
  )

private val HighContrastColorScheme =
  darkColorScheme(
    primary = HighContrastPrimary,
    secondary = HighContrastSecondary,
    background = HighContrastBackground,
    surface = HighContrastSurface,
    onBackground = HighContrastOnBackground,
    onSurface = HighContrastOnSurface,
    surfaceVariant = HighContrastSurface,
    onSurfaceVariant = HighContrastSecondary,
    primaryContainer = HighContrastSurface,
    onPrimaryContainer = HighContrastPrimary
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  highContrast: Boolean = false,
  dynamicColor: Boolean = false, // Disable dynamic colors by default to preserve custom branding
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      highContrast -> HighContrastColorScheme
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

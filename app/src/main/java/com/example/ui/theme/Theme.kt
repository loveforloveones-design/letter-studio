package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme =
  lightColorScheme(
    primary = JpInk,
    onPrimary = JpPaperCream,
    secondary = JpAccentCoral,
    onSecondary = JpInk,
    tertiary = JpAccentYellow,
    onTertiary = JpInk,
    background = JpPinkBg,
    onBackground = JpInk,
    surface = JpPaperCream,
    onSurface = JpInk,
    surfaceVariant = JpPaperVintage,
    onSurfaceVariant = JpInkMedium,
    outline = JpInk,
  )

private val DarkColorScheme =
  darkColorScheme(
    primary = JpPaperCream,
    onPrimary = JpInk,
    secondary = JpAccentPink,
    onSecondary = JpInk,
    background = JpInk,
    onBackground = JpPaperCream,
    surface = Color(0xFF28263E),
    onSurface = JpPaperCream,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

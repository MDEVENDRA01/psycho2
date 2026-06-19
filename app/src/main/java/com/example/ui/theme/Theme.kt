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

private val DarkColorScheme =
  darkColorScheme(
    primary = SerenePrimary,
    secondary = SereneSecondary,
    background = SereneOnBackground,
    surface = SereneOnBackground,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
  )

private val LightColorScheme =
  lightColorScheme(
    primary = SerenePrimary,
    secondary = SereneSecondary,
    background = SereneBackground,
    surface = SereneSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = SereneOnBackground,
    onSurface = SereneOnSurface,
    onSurfaceVariant = SereneOnSurfaceVariant,
    outline = SereneOutline,
    outlineVariant = SereneOutlineVariant,
    errorContainer = SereneErrorContainer,
    onErrorContainer = SereneOnErrorContainer,
    primaryContainer = SerenePrimaryContainer,
    onPrimaryContainer = SereneOnPrimaryContainer,
    secondaryContainer = SereneSecondaryContainer,
    onSecondaryContainer = SereneOnSecondaryContainer,
    surfaceVariant = SereneSurfaceVariant,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(colorScheme = LightColorScheme, typography = Typography, content = content)
}

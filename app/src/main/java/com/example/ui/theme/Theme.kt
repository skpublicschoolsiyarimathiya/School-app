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

private val DarkColorScheme = darkColorScheme(
  primary = RoyalBlue80,
  onPrimary = RoyalBlue20,
  primaryContainer = RoyalBlue40,
  onPrimaryContainer = RoyalBlue90,
  secondary = Gold80,
  onSecondary = Gold30,
  secondaryContainer = Gold40,
  onSecondaryContainer = GoldContainerLight,
  tertiary = Emerald80,
  background = Slate900,
  surface = Slate800,
  surfaceVariant = Color(0xFF334155),
  onBackground = Slate50,
  onSurface = Slate50
)

private val LightColorScheme = lightColorScheme(
  primary = RoyalBlue40,
  onPrimary = Color.White,
  primaryContainer = RoyalBlue90,
  onPrimaryContainer = RoyalBlue20,
  secondary = Gold40,
  onSecondary = Color.White,
  secondaryContainer = GoldContainerLight,
  onSecondaryContainer = GoldOnContainerLight,
  tertiary = Emerald40,
  onTertiary = Color.White,
  tertiaryContainer = EmeraldContainerLight,
  background = Slate50,
  surface = Color.White,
  surfaceVariant = Slate100,
  onBackground = Slate900,
  onSurface = Slate900
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our signature academic theme by default
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

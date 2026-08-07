package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NexusColorScheme = darkColorScheme(
    primary = ElectricLime,
    onPrimary = CyberBlack,
    primaryContainer = CyberSurfaceHeader,
    onPrimaryContainer = ElectricLime,
    secondary = CyberCyan,
    onSecondary = CyberBlack,
    secondaryContainer = CyberSurfaceVariant,
    onSecondaryContainer = CyberCyan,
    tertiary = StatusConditional,
    onTertiary = CyberBlack,
    background = CyberBackground,
    onBackground = OffWhiteText,
    surface = CyberSurface,
    onSurface = OffWhiteText,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = MutedText,
    outline = MutedBorder,
    error = StatusUnsupported,
    onError = CyberBlack
)

@Composable
fun NexusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NexusColorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility alias for template
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    NexusTheme(darkTheme = darkTheme, content = content)
}

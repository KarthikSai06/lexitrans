package com.example.lexitrans.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ProfessionalLightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = OnNavyPrimary,
    primaryContainer = NavyContainer,
    onPrimaryContainer = OnNavyContainer,
    secondary = TealSecondary,
    onSecondary = OnTealSecondary,
    secondaryContainer = TealContainer,
    onSecondaryContainer = OnTealContainer,
    tertiary = IndigoTertiary,
    onTertiary = OnIndigoTertiary,
    tertiaryContainer = IndigoContainer,
    onTertiaryContainer = OnIndigoContainer,
    background = LightBackground,
    onBackground = TextPrimary,
    surface = LightSurface,
    onSurface = TextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BorderStroke,
    outlineVariant = BorderVariant,
    error = RoseError,
    onError = LightSurface,
    errorContainer = RoseContainer,
    onErrorContainer = RoseError
)

@Composable
fun LexiTransTheme(
    // Force light theme as requested by user to maintain professional light UI
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ProfessionalLightColorScheme,
        typography = Typography,
        content = content
    )
}

package com.linguafranca.ui.theme

import androidx.compose.ui.graphics.Color

// Franco the Parrot inspired colors - Eclectus parrot green with tropical accents
object LinguaFrancaColors {
    // Primary - Eclectus Parrot Green
    val ParrotGreen = Color(0xFF2E7D32)
    val ParrotGreenLight = Color(0xFF4CAF50)
    val ParrotGreenDark = Color(0xFF1B5E20)
    
    // Secondary - Tropical Orange/Red (male eclectus accent)
    val TropicalOrange = Color(0xFFE65100)
    val TropicalOrangeLight = Color(0xFFFF8A50)
    val CoralRed = Color(0xFFE53935)
    
    // Accent - Tropical Blue
    val TropicalBlue = Color(0xFF0288D1)
    val TropicalBlueLight = Color(0xFF03A9F4)
    
    // Backgrounds
    val CreamWhite = Color(0xFFFFFBF5)
    val WarmWhite = Color(0xFFFFF8E1)
    val DarkForest = Color(0xFF121A12)
    val DarkSurface = Color(0xFF1E2A1E)
    
    // Neutrals
    val CharcoalGray = Color(0xFF2D3A2D)
    val MossGray = Color(0xFF4A5D4A)
    val SageGray = Color(0xFF8FA18F)
    val LightSage = Color(0xFFD5E3D5)
}

// Light theme colors
val md_theme_light_primary = LinguaFrancaColors.ParrotGreen
val md_theme_light_onPrimary = Color.White
val md_theme_light_primaryContainer = Color(0xFFC8E6C9)
val md_theme_light_onPrimaryContainer = LinguaFrancaColors.ParrotGreenDark

val md_theme_light_secondary = LinguaFrancaColors.TropicalOrange
val md_theme_light_onSecondary = Color.White
val md_theme_light_secondaryContainer = Color(0xFFFFE0B2)
val md_theme_light_onSecondaryContainer = Color(0xFFBF360C)

val md_theme_light_tertiary = LinguaFrancaColors.TropicalBlue
val md_theme_light_onTertiary = Color.White
val md_theme_light_tertiaryContainer = Color(0xFFB3E5FC)
val md_theme_light_onTertiaryContainer = Color(0xFF01579B)

val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_onError = Color.White
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onErrorContainer = Color(0xFF410002)

val md_theme_light_background = LinguaFrancaColors.CreamWhite
val md_theme_light_onBackground = LinguaFrancaColors.CharcoalGray
val md_theme_light_surface = Color.White
val md_theme_light_onSurface = LinguaFrancaColors.CharcoalGray
val md_theme_light_surfaceVariant = LinguaFrancaColors.LightSage
val md_theme_light_onSurfaceVariant = LinguaFrancaColors.MossGray
val md_theme_light_outline = LinguaFrancaColors.SageGray
val md_theme_light_outlineVariant = Color(0xFFC4D3C4)

val md_theme_light_inverseSurface = LinguaFrancaColors.CharcoalGray
val md_theme_light_inverseOnSurface = LinguaFrancaColors.LightSage
val md_theme_light_inversePrimary = LinguaFrancaColors.ParrotGreenLight

// Dark theme colors
val md_theme_dark_primary = LinguaFrancaColors.ParrotGreenLight
val md_theme_dark_onPrimary = LinguaFrancaColors.ParrotGreenDark
val md_theme_dark_primaryContainer = LinguaFrancaColors.ParrotGreen
val md_theme_dark_onPrimaryContainer = Color(0xFFC8E6C9)

val md_theme_dark_secondary = LinguaFrancaColors.TropicalOrangeLight
val md_theme_dark_onSecondary = Color(0xFF5C2300)
val md_theme_dark_secondaryContainer = LinguaFrancaColors.TropicalOrange
val md_theme_dark_onSecondaryContainer = Color(0xFFFFE0B2)

val md_theme_dark_tertiary = LinguaFrancaColors.TropicalBlueLight
val md_theme_dark_onTertiary = Color(0xFF003258)
val md_theme_dark_tertiaryContainer = LinguaFrancaColors.TropicalBlue
val md_theme_dark_onTertiaryContainer = Color(0xFFB3E5FC)

val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)

val md_theme_dark_background = LinguaFrancaColors.DarkForest
val md_theme_dark_onBackground = LinguaFrancaColors.LightSage
val md_theme_dark_surface = LinguaFrancaColors.DarkSurface
val md_theme_dark_onSurface = LinguaFrancaColors.LightSage
val md_theme_dark_surfaceVariant = LinguaFrancaColors.MossGray
val md_theme_dark_onSurfaceVariant = LinguaFrancaColors.SageGray
val md_theme_dark_outline = LinguaFrancaColors.SageGray
val md_theme_dark_outlineVariant = LinguaFrancaColors.MossGray

val md_theme_dark_inverseSurface = LinguaFrancaColors.LightSage
val md_theme_dark_inverseOnSurface = LinguaFrancaColors.CharcoalGray
val md_theme_dark_inversePrimary = LinguaFrancaColors.ParrotGreen

// Tag colors for word categorization
val TagColors = listOf(
    Color(0xFF4CAF50), // Green
    Color(0xFF2196F3), // Blue
    Color(0xFFF44336), // Red
    Color(0xFFFF9800), // Orange
    Color(0xFF9C27B0), // Purple
    Color(0xFF00BCD4), // Cyan
    Color(0xFFE91E63), // Pink
    Color(0xFF795548), // Brown
    Color(0xFF607D8B), // Blue Gray
    Color(0xFFFFEB3B)  // Yellow
)


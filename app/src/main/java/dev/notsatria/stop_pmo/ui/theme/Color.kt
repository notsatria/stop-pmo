package dev.notsatria.stop_pmo.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class CustomTheme(
    val surface: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val iconPrimary: Color,
    val iconDisabled: Color,
    val iconSecondary: Color,
    val buttonPrimary: Color,
    val buttonDisabled: Color,
    val isLight: Boolean
)

val lightThemeColors = CustomTheme(
    surface = Color(0xFFF6F7F8),
    textPrimary = Color(0xFF111827),
    textSecondary = Color(0xFF6C7381),
    iconPrimary = Color(0xFF6C7381),
    iconDisabled = Color(0xFF707785),
    iconSecondary = Color(0xFF717785),
    buttonPrimary = Color(0xFF6C7381),
    buttonDisabled = Color(0xFF707785),
    isLight = true
)


val darkThemeColors = CustomTheme(
    surface = Color(0xFF101C22),
    textPrimary = Color.White,
    textSecondary = Color(0xFF9CA3AF),
    iconPrimary = Color(0xFF1193D4),
    iconDisabled = Color(0xFF6B7280),
    iconSecondary = Color(0xFF989FAB),
    buttonPrimary = Color(0xFF1193D4),
    buttonDisabled = Color(0xFF6B7280),
    isLight = false
)

val LocalTheme = staticCompositionLocalOf {
    darkThemeColors
}
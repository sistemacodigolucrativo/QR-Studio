package br.com.universalbuild.core.designsystem.theme

import android.graphics.Color as AndroidColor
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val BrandPrimary = Color(0xFF2255FF)
private val LightSurface = Color(0xFFFFFFFF)
private val DarkSurface = Color(0xFF12141A)
private val SemanticError = Color(0xFFB3261E)

internal val LightScheme = lightColorScheme(
    primary = BrandPrimary,
    surface = LightSurface,
    error = SemanticError
)
internal val DarkScheme = darkColorScheme(
    primary = Color(0xFFAFC5FF),
    surface = DarkSurface,
    error = Color(0xFFFFB4AB)
)

internal fun brandColorScheme(base: ColorScheme, hex: String): ColorScheme {
    val brand = runCatching { Color(AndroidColor.parseColor(hex)) }.getOrNull() ?: return base
    return base.copy(primary = brand)
}

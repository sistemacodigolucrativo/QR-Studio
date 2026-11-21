package br.com.universalbuild.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun MasterTheme(brandColorHex: String, dynamicColor: Boolean = false, darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val context = LocalContext.current
    val palette = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> brandColorScheme(DarkScheme, brandColorHex)
        else -> brandColorScheme(LightScheme, brandColorHex)
    }
    MaterialTheme(
        colorScheme = palette,
        typography = MasterTypography,
        shapes = MasterShapes,
        content = content
    )
}

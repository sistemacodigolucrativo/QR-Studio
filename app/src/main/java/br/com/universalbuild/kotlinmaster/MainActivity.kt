package br.com.universalbuild.kotlinmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import br.com.universalbuild.core.designsystem.theme.MasterTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val widthClass = calculateWindowSizeClass(this).widthSizeClass
            MasterTheme(brandColorHex = BuildConfig.BRAND_COLOR) {
                MasterNavigation(isExpanded = widthClass >= WindowWidthSizeClass.Medium)
            }
        }
    }
}

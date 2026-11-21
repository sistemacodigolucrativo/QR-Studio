package br.com.universalbuild.kotlinmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import br.com.universalbuild.core.ads.AdMobFooter
import br.com.universalbuild.core.ads.AdMobRuntime
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
        AdMobRuntime.start(this)
        setContent {
            val widthClass = calculateWindowSizeClass(this).widthSizeClass
            MasterTheme(brandColorHex = BuildConfig.BRAND_COLOR) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        MasterNavigation(isExpanded = widthClass >= WindowWidthSizeClass.Medium)
                    }
                    val privacyRequired by AdMobRuntime.privacyOptionsRequired.collectAsState()
                    if (privacyRequired) {
                        TextButton(onClick = { AdMobRuntime.showPrivacyOptions(this@MainActivity) }) {
                            Text("Privacidade dos anúncios")
                        }
                    }
                    AdMobFooter()
                }
            }
        }
    }
}

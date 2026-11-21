package br.com.universalbuild.feature.home

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import br.com.universalbuild.core.designsystem.theme.MasterTheme
import br.com.universalbuild.feature.home.ui.HomeScreen
import br.com.universalbuild.feature.home.ui.HomeUiState
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule val rule = createComposeRule()

    @Test
    fun incrementActionIsVisible() {
        rule.setContent {
            MasterTheme(brandColorHex = "#2255FF") {
                HomeScreen(HomeUiState(), isExpanded = false, onIncrement = {})
            }
        }
        rule.onNodeWithTag("home-increment").assertExists()
    }
}

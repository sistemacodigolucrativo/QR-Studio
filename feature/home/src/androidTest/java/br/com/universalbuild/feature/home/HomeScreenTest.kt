package br.com.universalbuild.feature.home

import androidx.compose.ui.test.assertExists
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
    fun generateActionIsVisible() {
        rule.setContent {
            MasterTheme(brandColorHex = "#176FAF") {
                HomeScreen(
                    state = HomeUiState(),
                    isExpanded = false,
                    onInputChange = {},
                    onGenerate = {},
                    onOpenHistory = {},
                    onDeleteHistory = {},
                    onShare = {}
                )
            }
        }
        rule.onNodeWithTag("qr-input").assertExists()
        rule.onNodeWithTag("qr-generate").assertExists()
    }
}

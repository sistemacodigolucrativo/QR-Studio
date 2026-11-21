package br.com.universalbuild.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import br.com.universalbuild.core.designsystem.theme.MasterDimens
import br.com.universalbuild.feature.home.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoute(isExpanded: Boolean, viewModel: HomeViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()
    HomeScreen(state = state, isExpanded = isExpanded, onIncrement = viewModel::increment)
}

@Composable
fun HomeScreen(state: HomeUiState, isExpanded: Boolean, onIncrement: () -> Unit) {
    Scaffold { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding).padding(MasterDimens.spacingLarge),
            contentAlignment = Alignment.Center
        ) {
            val label: @Composable () -> Unit = {
                Text(
                    text = stringResource(R.string.home_counter, state.count),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            val action: @Composable () -> Unit = {
                Button(
                    onClick = onIncrement,
                    modifier = Modifier
                        .sizeIn(minHeight = MasterDimens.touchTarget)
                        .testTag("home-increment")
                ) {
                    Text(stringResource(R.string.home_increment))
                }
            }
            if (isExpanded) {
                Row(
                    modifier = Modifier.fillMaxWidth().sizeIn(maxWidth = MasterDimens.maxContentWidth),
                    horizontalArrangement = Arrangement.spacedBy(MasterDimens.spacingLarge),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    label()
                    action()
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(MasterDimens.spacingLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    label()
                    action()
                }
            }
        }
    }
}

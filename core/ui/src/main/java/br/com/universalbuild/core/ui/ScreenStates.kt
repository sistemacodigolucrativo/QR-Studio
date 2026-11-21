package br.com.universalbuild.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import br.com.universalbuild.core.common.UiState
import br.com.universalbuild.core.designsystem.theme.MasterDimens

@Composable
fun StandardState(state: UiState<Nothing>, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(MasterDimens.spacingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MasterDimens.spacingMedium)
    ) {
        when (state) {
            UiState.Loading -> CircularProgressIndicator()
            UiState.Empty -> Text(stringResource(R.string.state_empty))
            UiState.Offline -> Text(stringResource(R.string.state_offline))
            is UiState.Failure -> Text(stringResource(R.string.state_error))
            is UiState.Content -> Unit
        }
        if (state is UiState.Offline || state is UiState.Failure) {
            Button(onClick = onRetry, modifier = Modifier.sizeIn(minHeight = MasterDimens.touchTarget)) {
                Text(stringResource(R.string.action_retry))
            }
        }
    }
}

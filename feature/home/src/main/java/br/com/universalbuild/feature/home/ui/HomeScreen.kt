package br.com.universalbuild.feature.home.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.com.universalbuild.feature.home.R
import br.com.universalbuild.feature.home.domain.QrHistoryItem
import br.com.universalbuild.feature.home.domain.QrMatrix
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoute(isExpanded: Boolean, viewModel: HomeViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    HomeScreen(
        state = state,
        isExpanded = isExpanded,
        onInputChange = viewModel::updateInput,
        onGenerate = viewModel::generate,
        onOpenHistory = viewModel::reopen,
        onDeleteHistory = viewModel::deleteHistory,
        onShare = { payload ->
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, payload)
            }
            context.startActivity(Intent.createChooser(intent, null))
        }
    )
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    isExpanded: Boolean,
    onInputChange: (String) -> Unit,
    onGenerate: () -> Unit,
    onOpenHistory: (String) -> Unit,
    onDeleteHistory: (String) -> Unit,
    onShare: (String) -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = if (isExpanded) 720.dp else 480.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(stringResource(R.string.qr_title), style = MaterialTheme.typography.headlineMedium)
                Text(stringResource(R.string.qr_subtitle), style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = state.input,
                    onValueChange = onInputChange,
                    modifier = Modifier.fillMaxWidth().testTag("qr-input"),
                    label = { Text(stringResource(R.string.qr_input_label)) },
                    minLines = 2,
                    maxLines = 5
                )
                Button(
                    onClick = onGenerate,
                    enabled = !state.generating,
                    modifier = Modifier.fillMaxWidth().testTag("qr-generate")
                ) {
                    if (state.generating) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp))
                    } else {
                        Text(stringResource(R.string.qr_generate))
                    }
                }
                val errorMessage = when (state.error) {
                    QrError.EMPTY -> R.string.qr_error_empty
                    QrError.TOO_LONG -> R.string.qr_error_too_long
                    QrError.ENCODING_FAILED -> R.string.qr_error_generation
                    null -> null
                }
                if (errorMessage != null) {
                    Text(
                        stringResource(errorMessage),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                state.qrMatrix?.let { qr ->
                    val bitmap = remember(qr) { qr.toBitmap() }
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = stringResource(R.string.qr_result_description),
                        modifier = Modifier.size(256.dp)
                            .align(Alignment.CenterHorizontally)
                            .testTag("qr-result")
                    )
                    OutlinedButton(
                        onClick = { onShare(state.input.trim()) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.qr_share_text))
                    }
                }
                HorizontalDivider()
                Text(
                    stringResource(R.string.qr_history_title),
                    style = MaterialTheme.typography.titleLarge
                )
                if (state.history.isEmpty()) {
                    Text(stringResource(R.string.qr_history_empty))
                } else {
                    state.history.take(20).forEach { item ->
                        HistoryRow(item, onOpenHistory, onDeleteHistory)
                        HorizontalDivider()
                    }
                }
                Spacer(modifier = Modifier.size(8.dp))
            }
        }
    }
}

@Composable
private fun HistoryRow(
    item: QrHistoryItem,
    onOpenHistory: (String) -> Unit,
    onDeleteHistory: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            item.payload,
            modifier = Modifier.weight(1f)
                .clickable { onOpenHistory(item.payload) }
                .testTag("qr-history-item"),
            maxLines = 2
        )
        TextButton(onClick = { onDeleteHistory(item.id) }) {
            Text(stringResource(R.string.qr_remove))
        }
    }
}

private fun QrMatrix.toBitmap(): Bitmap {
    val argb = IntArray(pixels.size) { index ->
        if (pixels[index]) Color.BLACK else Color.WHITE
    }
    return Bitmap.createBitmap(argb, size, size, Bitmap.Config.ARGB_8888)
}

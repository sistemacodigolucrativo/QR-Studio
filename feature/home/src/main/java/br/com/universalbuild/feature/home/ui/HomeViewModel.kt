package br.com.universalbuild.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.universalbuild.feature.home.domain.GenerateQrCodeUseCase
import br.com.universalbuild.feature.home.domain.QrHistoryItem
import br.com.universalbuild.feature.home.domain.QrHistoryRepository
import br.com.universalbuild.feature.home.domain.QrMatrix
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class QrError { EMPTY, TOO_LONG, ENCODING_FAILED }

data class HomeUiState(
    val input: String = "",
    val qrMatrix: QrMatrix? = null,
    val history: List<QrHistoryItem> = emptyList(),
    val generating: Boolean = false,
    val error: QrError? = null
)

class HomeViewModel(
    private val generateQrCode: GenerateQrCodeUseCase,
    private val historyRepository: QrHistoryRepository
) : ViewModel() {
    private val input = MutableStateFlow("")
    private val matrix = MutableStateFlow<QrMatrix?>(null)
    private val generating = MutableStateFlow(false)
    private val error = MutableStateFlow<QrError?>(null)

    val uiState: StateFlow<HomeUiState> =
        combine(input, matrix, generating, error, historyRepository.observeRecent()) {
                value, image, isGenerating, failure, items ->
            HomeUiState(value, image, items, isGenerating, failure)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun updateInput(value: String) {
        input.value = value
        matrix.value = null
        error.value = null
    }

    fun generate() {
        if (generating.value) return
        val payload = input.value.trim()
        if (payload.isBlank()) {
            error.value = QrError.EMPTY
            return
        }
        if (payload.toByteArray(Charsets.UTF_8).size > GenerateQrCodeUseCase.MAX_INPUT_BYTES) {
            error.value = QrError.TOO_LONG
            return
        }
        viewModelScope.launch {
            generating.value = true
            error.value = null
            try {
                val encoded = withContext(Dispatchers.Default) { generateQrCode(payload) }
                matrix.value = encoded
                historyRepository.save(payload)
            } catch (_: Exception) {
                error.value = QrError.ENCODING_FAILED
            } finally {
                generating.value = false
            }
        }
    }

    fun reopen(payload: String) {
        updateInput(payload)
        generate()
    }

    fun deleteHistory(id: String) {
        viewModelScope.launch { historyRepository.delete(id) }
    }
}

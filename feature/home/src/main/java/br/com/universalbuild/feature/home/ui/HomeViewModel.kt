package br.com.universalbuild.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.universalbuild.feature.home.domain.IncrementCountUseCase
import br.com.universalbuild.feature.home.domain.ObserveCountUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(val count: Int = 0)

class HomeViewModel(
    observeCount: ObserveCountUseCase,
    private val incrementCount: IncrementCountUseCase
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> =
        observeCount().map { HomeUiState(count = it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun increment() {
        viewModelScope.launch { incrementCount() }
    }
}

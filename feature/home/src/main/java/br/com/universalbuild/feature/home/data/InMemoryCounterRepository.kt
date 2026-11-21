package br.com.universalbuild.feature.home.data

import br.com.universalbuild.feature.home.domain.CounterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/** Example data layer. Replace without changing screens, UseCases or shared modules. */
class InMemoryCounterRepository : CounterRepository {
    private val mutableCount = MutableStateFlow(0)
    override val count: StateFlow<Int> = mutableCount

    override suspend fun increment() {
        mutableCount.update { it + 1 }
    }
}

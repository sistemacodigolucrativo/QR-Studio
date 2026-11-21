package br.com.universalbuild.feature.home

import br.com.universalbuild.feature.home.data.InMemoryCounterRepository
import br.com.universalbuild.feature.home.domain.IncrementCountUseCase
import br.com.universalbuild.feature.home.domain.ObserveCountUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class CounterUseCasesTest {
    @Test
    fun countStartsEmptyAndIncrements() = runTest {
        val repository = InMemoryCounterRepository()
        val observe = ObserveCountUseCase(repository)
        val increment = IncrementCountUseCase(repository)
        assertEquals(0, observe().value)
        increment()
        assertEquals(1, observe().value)
    }
}

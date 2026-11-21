package br.com.universalbuild.feature.home.domain

import kotlinx.coroutines.flow.StateFlow

class ObserveCountUseCase(private val repository: CounterRepository) {
    operator fun invoke(): StateFlow<Int> = repository.count
}

class IncrementCountUseCase(private val repository: CounterRepository) {
    suspend operator fun invoke() = repository.increment()
}

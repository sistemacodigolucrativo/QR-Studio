package br.com.universalbuild.feature.home.domain

import kotlinx.coroutines.flow.StateFlow

interface CounterRepository {
    val count: StateFlow<Int>
    suspend fun increment()
}

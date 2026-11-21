package br.com.universalbuild.feature.home.di

import br.com.universalbuild.feature.home.data.InMemoryCounterRepository
import br.com.universalbuild.feature.home.domain.CounterRepository
import br.com.universalbuild.feature.home.domain.IncrementCountUseCase
import br.com.universalbuild.feature.home.domain.ObserveCountUseCase
import br.com.universalbuild.feature.home.ui.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module {
    single<CounterRepository> { InMemoryCounterRepository() }
    factory { ObserveCountUseCase(get()) }
    factory { IncrementCountUseCase(get()) }
    viewModelOf(::HomeViewModel)
}

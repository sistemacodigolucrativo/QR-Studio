package br.com.universalbuild.feature.home.di

import br.com.universalbuild.core.database.MasterDatabase
import br.com.universalbuild.feature.home.data.RoomQrHistoryRepository
import br.com.universalbuild.feature.home.data.ZxingQrCodeEncoder
import br.com.universalbuild.feature.home.domain.GenerateQrCodeUseCase
import br.com.universalbuild.feature.home.domain.QrCodeEncoder
import br.com.universalbuild.feature.home.domain.QrHistoryRepository
import br.com.universalbuild.feature.home.ui.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module {
    single<QrCodeEncoder> { ZxingQrCodeEncoder() }
    single<QrHistoryRepository> { RoomQrHistoryRepository(get<MasterDatabase>().savedItemDao()) }
    factory { GenerateQrCodeUseCase(get()) }
    viewModelOf(::HomeViewModel)
}

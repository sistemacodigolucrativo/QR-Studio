package br.com.universalbuild.kotlinmaster

import android.app.Application
import androidx.room.Room
import br.com.universalbuild.core.ads.AdMobConfig
import br.com.universalbuild.core.ads.AdMobRuntime
import br.com.universalbuild.core.database.DatabaseMigrations
import br.com.universalbuild.core.database.MasterDatabase
import br.com.universalbuild.core.network.NetworkClientFactory
import br.com.universalbuild.feature.home.di.homeModule
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import timber.log.Timber

class MasterApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        AdMobRuntime.configure(
            AdMobConfig(
                mode = BuildConfig.ADS_MODE,
                appId = BuildConfig.ADS_APP_ID,
                bannerEnabled = BuildConfig.ADS_BANNER_ENABLED,
                bannerId = BuildConfig.ADS_BANNER_ID,
                interstitialEnabled = BuildConfig.ADS_INTERSTITIAL_ENABLED,
                interstitialId = BuildConfig.ADS_INTERSTITIAL_ID,
                rewardedEnabled = BuildConfig.ADS_REWARDED_ENABLED,
                rewardedId = BuildConfig.ADS_REWARDED_ID,
                interstitialCooldownSeconds = BuildConfig.ADS_COOLDOWN_SECONDS
            )
        )
        startKoin {
            androidContext(this@MasterApplication)
            modules(
                homeModule,
                module {
                    single<OkHttpClient> { NetworkClientFactory.create(BuildConfig.DEBUG) }
                    single {
                        Retrofit.Builder()
                            .baseUrl(BuildConfig.BASE_API_URL)
                            .client(get<OkHttpClient>())
                            .build()
                    }
                    single {
                        Room.databaseBuilder(androidContext(), MasterDatabase::class.java, "master.db")
                            .apply {
                                DatabaseMigrations.ALL.forEach { migration ->
                                    addMigrations(migration)
                                }
                            }.build()
                    }
                }
            )
        }
    }
}

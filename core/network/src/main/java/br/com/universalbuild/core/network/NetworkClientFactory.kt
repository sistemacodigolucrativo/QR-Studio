package br.com.universalbuild.core.network

import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

/** Never logs request bodies or authorization headers. */
object NetworkClientFactory {
    fun create(debug: Boolean): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .apply {
            if (debug) {
                addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    }
                )
            }
        }.build()
}

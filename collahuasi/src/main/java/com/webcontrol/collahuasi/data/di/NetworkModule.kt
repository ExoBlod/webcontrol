package com.webcontrol.collahuasi.data.di

import android.content.Context
import com.webcontrol.collahuasi.BuildConfig
import com.webcontrol.collahuasi.R
import com.webcontrol.collahuasi.domain.common.PREF_API_URL
import com.webcontrol.collahuasi.data.network.Api
import com.webcontrol.collahuasi.data.network.AuthInterceptor
import com.webcontrol.core.utils.LocalStorage
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single { createHttpClient(get()) }
    single { createWebService<Api>(get(), get(), androidContext()) }
}

fun createHttpClient(localStorage: LocalStorage): OkHttpClient {
    val httpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

    if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        httpClientBuilder.addInterceptor(loggingInterceptor)
    }

    httpClientBuilder.addInterceptor(AuthInterceptor(localStorage))

    return httpClientBuilder.build()
}

inline fun <reified T> createWebService(
        httpClient: OkHttpClient,
        localStorage: LocalStorage,
        context: Context
): T {
    val url = localStorage[PREF_API_URL, context.getString(R.string.api_url)]!!
    val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    return retrofit.create(T::class.java)
}
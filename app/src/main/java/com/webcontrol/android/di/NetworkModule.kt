package com.webcontrol.android.di

import android.content.Context
import com.webcontrol.android.BuildConfig
import com.webcontrol.android.R
import com.webcontrol.android.data.*
import com.webcontrol.android.data.network.AuthInterceptor
import com.webcontrol.android.util.Constants
import com.webcontrol.android.util.LocalStorage
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {

    factory { createHttpClient() }

    single { createWebservice<RestInterfaceAnglo>(get(), androidContext(), get()) }
    single { createWebservice<RestInterfaceCollahuasi>(get(), androidContext(), get()) }
    single { createWebservice<RestInterfaceBambas>(get(), androidContext(), get()) }
    single { createWebservice<RestInterfaceLaPoderosa>(get(), androidContext(), get()) }
    single { createWebservice<RestInterfacePHC>(get(), androidContext(), get()) }
}

inline fun <reified T> createWebservice(
    httpClientBuilder: OkHttpClient.Builder,
    context: Context,
    localStorage: LocalStorage
): T {
    val url: String = when (T::class.java) {
        RestInterfaceAnglo::class.java -> {
            httpClientBuilder.addInterceptor(AuthInterceptor(localStorage, Constants.TOKEN_ANGLO))
            context.getString(R.string.ws_url_anglo)
        }

        RestInterfaceCollahuasi::class.java -> {
            httpClientBuilder.addInterceptor(
                AuthInterceptor(
                    localStorage,
                    Constants.TOKEN_COLLAHUASI
                )
            )
            context.getString(R.string.ws_url_collahuasi)
        }

        RestInterfaceBambas::class.java -> {
            httpClientBuilder.addInterceptor(AuthInterceptor(localStorage, Constants.TOKEN_BAMBAS))
            context.getString(R.string.ws_url_bambas)
        }

        RestInterfaceLaPoderosa::class.java -> {
            httpClientBuilder.addInterceptor(
                AuthInterceptor(
                    localStorage,
                    Constants.TOKEN_PODEROSA
                )
            )
            context.getString(R.string.ws_url_poderosa)
        }

        RestInterfacePHC::class.java -> {
            httpClientBuilder.addInterceptor(
                AuthInterceptor(
                    localStorage,
                    Constants.TOKEN_AUTHORIZATION_PHC
                )
            )
            context.getString(R.string.ws_url_phc)
        }

        else -> throw IllegalArgumentException("Client ${T::class.java.simpleName} is not implemented yet!!!")
    }

    if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        httpClientBuilder.addInterceptor(loggingInterceptor)
    }

    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .client(httpClientBuilder.build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    return retrofit.create(T::class.java)
}

fun createHttpClient(): OkHttpClient.Builder {
    return OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
}


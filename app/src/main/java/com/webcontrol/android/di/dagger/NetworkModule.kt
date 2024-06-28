package com.webcontrol.android.di.dagger

import android.content.Context
import com.webcontrol.android.R
import com.webcontrol.android.data.RestInterfaceAnglo
import com.webcontrol.android.data.RestInterfaceBambas
import com.webcontrol.android.data.network.RequestHeaderInterceptor
import com.webcontrol.android.data.network.RequestHeaderInterceptorBambas
import com.webcontrol.angloamerican.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object  NetworkModule {
    @Provides
    @Singleton
    fun provideApi(@ApplicationContext context: Context): RestInterfaceAnglo {
        val httpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            httpClientBuilder.addInterceptor(loggingInterceptor)
        }

        httpClientBuilder.addInterceptor(RequestHeaderInterceptor())

        val httpClient = httpClientBuilder.build()
        val url = context.getString(R.string.ws_url_anglo)
        return Retrofit.Builder()
            .baseUrl(url)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RestInterfaceAnglo::class.java)
    }

    @Provides
    @Singleton
    fun provideApiBambas(@ApplicationContext context: Context): RestInterfaceBambas {
        val httpClientBuilder = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            httpClientBuilder.addInterceptor(loggingInterceptor)
        }

        httpClientBuilder.addInterceptor(RequestHeaderInterceptorBambas())

        val httpClient = httpClientBuilder.build()
        val url = context.getString(R.string.ws_url_bambas)
        return Retrofit.Builder()
            .baseUrl(url)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RestInterfaceBambas::class.java)
    }
}
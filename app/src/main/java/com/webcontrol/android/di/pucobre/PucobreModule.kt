package com.webcontrol.android.di.pucobre

import com.webcontrol.core.utils.LocalStorage
import com.webcontrol.pucobre.data.PucobreApiService
import com.webcontrol.pucobre.data.network.AuthInterceptor
import com.webcontrol.pucobre.data.repositories.*
import com.webcontrol.pucobre.domain.usecases.GetCredentialUseCase
import com.webcontrol.pucobre.domain.usecases.GetTokenUseCase
import com.webcontrol.pucobre.domain.usecases.GetWorkerUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PucobreModule {
    @Provides
    fun provideAuthInterceptor(localStorage: LocalStorage): AuthInterceptor {
        return AuthInterceptor(localStorage)
    }

    @Singleton
    @Provides
    fun providePucobreApiService(@Named("pucobre") retrofit: Retrofit): PucobreApiService {
        return retrofit.create(PucobreApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideSecurityRepository(apiService: PucobreApiService): ISecurityRepository {
        return SecurityRepository(apiService)
    }

    @Provides
    fun provideGetTokenUseCase(repository: ISecurityRepository): GetTokenUseCase {
        return GetTokenUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideCredentialRepository(apiService: PucobreApiService): ICredentialRepository {
        return CredentialRepository(apiService)
    }

    @Provides
    fun provideGetCredentialUseCase(repository: ICredentialRepository): GetCredentialUseCase {
        return GetCredentialUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideWorkerRepository(apiService: PucobreApiService): IWorkerRepository {
        return WorkerRepository(apiService)
    }

    @Provides
    fun provideGetWorkerUseCase(repository: IWorkerRepository): GetWorkerUseCase {
        return GetWorkerUseCase(repository)
    }
}
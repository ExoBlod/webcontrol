package com.webcontrol.android.di.dagger

import com.webcontrol.android.angloamerican.data.repositories.CourseRepository
import com.webcontrol.android.angloamerican.data.repositories.ReservaBusRepository
import com.webcontrol.android.bambas.repositories.NewCheckListRepository
import com.webcontrol.android.data.RestInterfaceAnglo
import com.webcontrol.android.data.RestInterfaceBambas
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)

object RepositoryModule {
    @Provides
    fun provideDateTimeRepository(api: RestInterfaceAnglo): CourseRepository {
        return CourseRepository(api)
    }

    @Provides
    fun provideDateTimeRepository2(api: RestInterfaceAnglo): ReservaBusRepository {
        return ReservaBusRepository(api)
    }

    @Provides
    fun provideNewChecklistRepository(api: RestInterfaceBambas): NewCheckListRepository {
        return NewCheckListRepository(api)
    }
}
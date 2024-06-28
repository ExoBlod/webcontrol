package com.webcontrol.android.di

import androidx.room.Room
import com.webcontrol.android.data.db.AppDataBase
import com.webcontrol.android.data.db.AppDataBase.Companion.DB_NAME
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(androidApplication(), AppDataBase::class.java, DB_NAME)
            .fallbackToDestructiveMigration().build()
    }

    single { get<AppDataBase>().preaccesoDao() }
    single { get<AppDataBase>().checkListDao() }
    single { get<AppDataBase>().preaccesoDetalleDao() }

}
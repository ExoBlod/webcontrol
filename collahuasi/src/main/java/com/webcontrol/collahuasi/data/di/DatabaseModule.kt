package com.webcontrol.collahuasi.data.di

import androidx.room.Room
import com.webcontrol.collahuasi.domain.common.DB_NAME
import com.webcontrol.collahuasi.data.database.AppDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(androidApplication(), AppDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration().build()
    }

    single { get<AppDatabase>().attendanceDao() }
}
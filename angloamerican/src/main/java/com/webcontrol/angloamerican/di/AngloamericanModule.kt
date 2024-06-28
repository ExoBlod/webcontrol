package com.webcontrol.angloamerican.di

import com.webcontrol.angloamerican.ui.checklistpreuso.data.db.AppDataBase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val angloAmericanModule = module {
    single { AppDataBase.getInstance(androidContext()) }
}
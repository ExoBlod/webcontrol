package com.webcontrol.android.di

import com.webcontrol.android.util.LocalStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { LocalStorage(androidContext()) }
}
package com.webcontrol.core.di

import com.webcontrol.core.utils.LocalStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {
    single { LocalStorage(androidContext()) }
}
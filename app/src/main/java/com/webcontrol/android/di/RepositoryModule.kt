package com.webcontrol.android.di

import com.webcontrol.android.data.clientRepositories.*
import com.webcontrol.android.ui.preacceso.PreaccesoRepository
import com.webcontrol.android.ui.preacceso.PreaccesoRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<PreaccesoRepository> { PreaccesoRepositoryImpl(get(), get(), get(), get()) }
    single<CollahuasiRepository> { CollahuasiRepositoryImpl(get()) }
    single<BambasRepository> { BambasRepositoryImpl(get()) }
    single<LaPoderosaRepository> { LaPoderosaRepositoryImpl(get()) }
    single<AngloRepository> { AngloRepositoryImpl(get()) }
    single<PHCRepository> { PHCRepositoryImpl(get()) }
}
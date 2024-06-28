package com.webcontrol.collahuasi

import com.webcontrol.collahuasi.data.di.databaseModule
import com.webcontrol.collahuasi.data.di.networkModule
import com.webcontrol.collahuasi.data.di.repositoryModule
import com.webcontrol.collahuasi.domain.di.interactionModule
import com.webcontrol.collahuasi.presentation.di.presentationModule

val collahuasiModule = listOf(databaseModule, interactionModule, networkModule, presentationModule, repositoryModule)
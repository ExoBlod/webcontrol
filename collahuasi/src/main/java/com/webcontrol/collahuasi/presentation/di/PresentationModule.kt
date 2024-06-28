package com.webcontrol.collahuasi.presentation.di

import com.webcontrol.collahuasi.presentation.attendance.GetAttendanceViewModel
import com.webcontrol.collahuasi.presentation.authentication.AuthenticationViewModel
import com.webcontrol.collahuasi.presentation.worker.GetWorkerViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { AuthenticationViewModel(get()) }
    viewModel { GetWorkerViewModel(get()) }
    viewModel { GetAttendanceViewModel(get()) }
}
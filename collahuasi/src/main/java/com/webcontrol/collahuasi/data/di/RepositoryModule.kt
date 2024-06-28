package com.webcontrol.collahuasi.data.di

import com.webcontrol.collahuasi.data.attendance.AttendanceRepository
import com.webcontrol.collahuasi.data.authentication.AuthenticationRepository
import com.webcontrol.collahuasi.data.worker.WorkerRepository
import com.webcontrol.collahuasi.domain.attendance.IAttendanceRepository
import com.webcontrol.collahuasi.domain.authentication.IAuthenticationRepository
import com.webcontrol.collahuasi.domain.worker.IWorkerRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory<IAuthenticationRepository> { AuthenticationRepository(get()) }
    factory<IWorkerRepository> { WorkerRepository(get()) }
    factory<IAttendanceRepository> { AttendanceRepository(get()) }
}
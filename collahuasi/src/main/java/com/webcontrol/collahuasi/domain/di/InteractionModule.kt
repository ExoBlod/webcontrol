package com.webcontrol.collahuasi.domain.di

import com.webcontrol.collahuasi.domain.attendance.GetAttendance
import com.webcontrol.collahuasi.domain.attendance.IGetAttendance
import com.webcontrol.collahuasi.domain.attendance.IInsertAttendance
import com.webcontrol.collahuasi.domain.attendance.InsertAttendance
import com.webcontrol.collahuasi.domain.authentication.Authenticate
import com.webcontrol.collahuasi.domain.authentication.IAuthenticate
import com.webcontrol.collahuasi.domain.worker.GetWorker
import com.webcontrol.collahuasi.domain.worker.IGetWorker
import org.koin.dsl.module

val interactionModule = module {
    factory<IAuthenticate> { Authenticate(get()) }
    factory<IGetWorker> { GetWorker(get()) }
    factory<IGetAttendance> { GetAttendance(get()) }
    factory<IInsertAttendance> { InsertAttendance(get()) }
}
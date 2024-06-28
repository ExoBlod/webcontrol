package com.webcontrol.collahuasi.data.network

import com.webcontrol.collahuasi.domain.attendance.Attendance
import com.webcontrol.collahuasi.domain.attendance.AttendanceResponse
import com.webcontrol.collahuasi.domain.authentication.AuthenticationRequest
import com.webcontrol.collahuasi.domain.authentication.AuthenticationResponse
import com.webcontrol.collahuasi.domain.worker.Worker
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Api {

    @GET("app/check")
    suspend fun check(): String

    @POST("auth")
    suspend fun authenticate(@Body request: AuthenticationRequest): AuthenticationResponse

    @GET("worker/info")
    suspend fun getWorker(@Query("workerId") workerId: String): List<Worker>

    @GET("assistance")
    suspend fun getAttendance(@Query("workerId") workerId: String): List<Attendance>

    @POST("assistance/insert")
    suspend fun insertAttendance(@Body attendance: Attendance): AttendanceResponse
}
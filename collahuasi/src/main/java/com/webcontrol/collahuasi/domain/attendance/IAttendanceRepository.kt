package com.webcontrol.collahuasi.domain.attendance

interface IAttendanceRepository {

    suspend fun getAttendance(workerId: String): List<Attendance>

    suspend fun insertAttendance(attendance: Attendance): AttendanceResponse
}
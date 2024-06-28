package com.webcontrol.collahuasi.data.attendance

import com.webcontrol.collahuasi.data.network.Api
import com.webcontrol.collahuasi.domain.attendance.Attendance
import com.webcontrol.collahuasi.domain.attendance.AttendanceResponse
import com.webcontrol.collahuasi.domain.attendance.IAttendanceRepository

class AttendanceRepository(private val api: Api) : IAttendanceRepository {

    override suspend fun getAttendance(workerId: String): List<Attendance> {
        return api.getAttendance(workerId)
    }

    override suspend fun insertAttendance(attendance: Attendance): AttendanceResponse {
        return api.insertAttendance(attendance)
    }
}
package com.webcontrol.collahuasi.domain.attendance

interface IInsertAttendance {

    suspend operator fun invoke(attendance: Attendance): AttendanceResponse
}

class InsertAttendance(private val repository: IAttendanceRepository) : IInsertAttendance {

    override suspend fun invoke(attendance: Attendance): AttendanceResponse {
        return repository.insertAttendance(attendance)
    }
}
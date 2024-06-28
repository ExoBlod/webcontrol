package com.webcontrol.collahuasi.domain.attendance

interface IGetAttendance {

    suspend operator fun invoke(workerId: String): List<Attendance>
}

class GetAttendance(private val repository: IAttendanceRepository) :IGetAttendance{

    override suspend fun invoke(workerId: String): List<Attendance> {
        return repository.getAttendance(workerId)
    }
}
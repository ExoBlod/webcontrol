package com.webcontrol.angloamerican.data.network.response

data class BookedCoursesData(
    var WorkerName: String,
    val ProgramId: Int,
    val ExamId: Int,
    val CharlaName: String,
    val CourseId: Int,
    val StartDate: String,
    val StartHour: String,
    val ReservationDate: String,
    val ReservationHour: String,
    val EndDate: String,
    val EndHour: String,
    val Validity: Int,
    val Note: Int,
    val CourseStatus: String,
    val Attended: String,
    val Duration: String,
    val Capacity: String,
    val Quantity: String
)
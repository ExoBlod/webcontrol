package com.webcontrol.angloamerican.ui.bookcourses.data

import com.webcontrol.angloamerican.data.network.response.QuestionData

data class ReserveCourseData(
    var idProgram: Int = 0,
    var idExam: Int = 0,
    var workerId: String = "",
    var idEnterprise: String = "",
    var filterRC: Int = 0,
    var idCourse: Int = 0,
    var list: List<QuestionData> = listOf()
)

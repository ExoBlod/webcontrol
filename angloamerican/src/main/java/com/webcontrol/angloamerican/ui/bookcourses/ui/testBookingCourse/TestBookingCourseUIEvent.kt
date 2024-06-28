package com.webcontrol.angloamerican.ui.bookcourses.ui.testBookingCourse

import com.webcontrol.angloamerican.data.network.response.QuestionData
import com.webcontrol.angloamerican.data.network.response.ResultExam

sealed class TestBookingCourseUIEvent {
    data class Success(val listQuestions: List<QuestionData>) : TestBookingCourseUIEvent()
    data class SuccessSendQuestionList(val resultExam: ResultExam) : TestBookingCourseUIEvent()
    object Error : TestBookingCourseUIEvent()
    object ShowLoading : TestBookingCourseUIEvent()
    object HideLoading : TestBookingCourseUIEvent()
}

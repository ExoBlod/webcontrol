package com.webcontrol.angloamerican.data.repositories

import com.webcontrol.angloamerican.data.dto.SendQuestionsRequest
import com.webcontrol.angloamerican.data.network.response.BookedCoursesData
import com.webcontrol.angloamerican.data.network.response.CourseContentData
import com.webcontrol.angloamerican.data.network.response.CourseData
import com.webcontrol.angloamerican.data.network.response.HistoryBookCourseData
import com.webcontrol.angloamerican.data.network.response.QuestionData
import com.webcontrol.angloamerican.data.network.response.ResultExam
import dagger.hilt.android.lifecycle.HiltViewModel

interface ICoursesRepository {
    suspend fun getCourseList(): List<CourseData>
    suspend fun getHistoryBookCoursesList(workerId: String): List<HistoryBookCourseData>
    suspend fun getBookedCoursesList(workerId: String): List<BookedCoursesData>
    suspend fun sendInputCourse(workerId: String, idCourse: Int, startDate: String): Boolean
    suspend fun getQuestionCourse(idExamen: Int): List<QuestionData>
    suspend fun postAnswers(
        idCharla: Int,
        idWorker: String,
        enterprise: String,
        listQuestions: List<QuestionData>
    ): ResultExam

    suspend fun getContentCourse(idCourseProg: Int, idExam: Int): List<CourseContentData>
}
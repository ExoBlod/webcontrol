package com.webcontrol.angloamerican.data.repositories

import com.webcontrol.angloamerican.data.dto.ReservationRequest
import com.webcontrol.angloamerican.data.dto.SendQuestionsRequest
import com.webcontrol.angloamerican.data.network.response.BookedCoursesData
import com.webcontrol.angloamerican.data.network.response.CourseContentData
import com.webcontrol.angloamerican.data.network.response.CourseData
import com.webcontrol.angloamerican.data.network.response.HistoryBookCourseData
import com.webcontrol.angloamerican.data.network.response.QuestionData
import com.webcontrol.angloamerican.data.network.response.ResultExam
import com.webcontrol.angloamerican.data.network.service.AngloamericanApiService
import javax.inject.Inject

class CoursesRepository @Inject constructor(private val apiService: AngloamericanApiService) :
    ICoursesRepository {
    override suspend fun getCourseList(): List<CourseData> {
        val response = apiService.getCourseList()
        return response.data
    }

    override suspend fun getHistoryBookCoursesList(workerId: String): List<HistoryBookCourseData> {
        val response = apiService.getHistoryBookCoursesList(workerId)
        return response.data
    }

    override suspend fun getBookedCoursesList(workerId: String): List<BookedCoursesData> {
        val response = apiService.getBookedCourses(workerId)
        return response.data
    }

    override suspend fun sendInputCourse(
        workerId: String,
        idCourse: Int,
        startDate: String
    ): Boolean {
        val response =
            apiService.postReservationCourse(
                ReservationRequest(
                    workerId, idCourse, startDate,
                    listOf(workerId)
                )
            )
        return response.data
    }

    override suspend fun getQuestionCourse(idExamen: Int): List<QuestionData> {
        val response = apiService.getQuestionCourse(idExamen)
        return response.data
    }

    override suspend fun getContentCourse(idCourseProg: Int, idExam: Int): List<CourseContentData> {
        val response = apiService.getContentCourse(idCourseProg, idExam)
        return response.data
    }

    override suspend fun postAnswers(
        idCharla: Int,
        idWorker: String,
        enterprise: String,
        listQuestions: List<QuestionData>
    ): ResultExam {
        val response = apiService.postAnswers(
            idCharla,
            idWorker,
            enterprise,
            listQuestions
        )
        return response.data
    }
}
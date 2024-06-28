package com.webcontrol.angloamerican.domain.usecases

import com.webcontrol.angloamerican.data.network.response.QuestionData
import com.webcontrol.angloamerican.data.repositories.ICoursesRepository
import javax.inject.Inject

class GetQuestionCourseUseCase @Inject constructor(private val repository: ICoursesRepository) {
    suspend operator fun invoke(idExamen: Int): List<QuestionData> =
        repository.getQuestionCourse(idExamen)
}
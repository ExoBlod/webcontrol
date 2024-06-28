package com.webcontrol.angloamerican.domain.usecases

import com.webcontrol.angloamerican.data.network.response.CourseContentData
import com.webcontrol.angloamerican.data.repositories.ICoursesRepository
import javax.inject.Inject

class GetCoursesContentUseCase @Inject constructor(private val repository: ICoursesRepository) {
    suspend operator fun invoke( idCourseProg: Int, idExamen: Int): List<CourseContentData> =
        repository.getContentCourse(idCourseProg,idExamen)
}
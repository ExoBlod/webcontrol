package com.webcontrol.angloamerican.domain.usecases

import com.webcontrol.angloamerican.data.repositories.ICoursesRepository
import javax.inject.Inject

class SendInputCourseUseCase @Inject constructor(private val repository: ICoursesRepository) {
    suspend operator fun invoke(workerId: String, idCourse: Int, startDate: String) =
        repository.sendInputCourse(workerId, idCourse, startDate)

}
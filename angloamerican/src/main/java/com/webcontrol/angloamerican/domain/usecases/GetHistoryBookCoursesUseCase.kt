package com.webcontrol.angloamerican.domain.usecases

import com.webcontrol.angloamerican.data.repositories.ICoursesRepository
import javax.inject.Inject

class GetHistoryBookCoursesUseCase @Inject constructor(private val repository: ICoursesRepository) {
    suspend operator fun invoke(workerId: String) = repository.getHistoryBookCoursesList(workerId)
}
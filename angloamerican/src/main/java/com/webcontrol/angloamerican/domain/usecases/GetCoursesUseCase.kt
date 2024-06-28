package com.webcontrol.angloamerican.domain.usecases

import com.webcontrol.angloamerican.data.repositories.ICoursesRepository
import javax.inject.Inject

class GetCoursesUseCase @Inject constructor(private val repository: ICoursesRepository) {
    suspend operator fun invoke() = repository.getCourseList()
}
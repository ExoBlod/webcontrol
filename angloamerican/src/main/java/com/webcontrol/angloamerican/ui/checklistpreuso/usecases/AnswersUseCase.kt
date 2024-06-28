package com.webcontrol.angloamerican.ui.checklistpreuso.usecases

import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.QuestionListResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.SaveAnswersRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.repository.IAnswersRepository
import javax.inject.Inject

class AnswersUseCase @Inject constructor(private val repository: IAnswersRepository) {
    suspend operator fun invoke(request: List<QuestionListResponse>) = repository.saveAnswers(request)
}
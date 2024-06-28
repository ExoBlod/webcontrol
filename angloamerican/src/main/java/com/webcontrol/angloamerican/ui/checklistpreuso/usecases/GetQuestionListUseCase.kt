package com.webcontrol.angloamerican.ui.checklistpreuso.usecases

import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.QuestionListRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.repository.IQuestionsRepository
import javax.inject.Inject

class GetQuestionListUseCase @Inject constructor(private val repository: IQuestionsRepository) {
    suspend operator fun invoke(request: String) = repository.getQuestions(request)
}
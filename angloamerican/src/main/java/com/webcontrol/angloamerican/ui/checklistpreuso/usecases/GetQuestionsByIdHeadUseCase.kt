package com.webcontrol.angloamerican.ui.checklistpreuso.usecases

import com.webcontrol.angloamerican.ui.checklistpreuso.data.repository.IQuestionsRepository
import javax.inject.Inject

class GetQuestionsByIdHeadUseCase @Inject constructor(private val repository: IQuestionsRepository) {
    suspend operator fun invoke(idHead: Int) = repository.getQuestionsByIdhead(idHead)
}
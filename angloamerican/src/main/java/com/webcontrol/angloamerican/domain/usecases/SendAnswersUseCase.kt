package com.webcontrol.angloamerican.domain.usecases

import com.webcontrol.angloamerican.data.dto.SendQuestionsRequest
import com.webcontrol.angloamerican.data.network.response.AnswerQuestionData
import com.webcontrol.angloamerican.data.network.response.QuestionData
import com.webcontrol.angloamerican.data.repositories.ICoursesRepository
import javax.inject.Inject

class SendAnswersUseCase @Inject constructor(private val repository: ICoursesRepository) {
    suspend operator fun invoke(
        idCharla: Int,
        idWorker: String,
        enterprise: String,
        listQuestions: List<QuestionData>
    ) =
        repository.postAnswers(
            idCharla,
            idWorker,
            enterprise,
            listQuestions
        )
}
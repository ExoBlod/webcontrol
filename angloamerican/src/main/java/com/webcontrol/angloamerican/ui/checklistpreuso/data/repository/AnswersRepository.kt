package com.webcontrol.angloamerican.ui.checklistpreuso.data.repository

import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.CheckListPreUsoApi
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.QuestionListResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.SaveAnswersRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.SaveAnswersResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.mapToSaveAnswersRequest
import javax.inject.Inject

interface IAnswersRepository{
    suspend fun saveAnswers(request: List<QuestionListResponse>): List<SaveAnswersResponse>
}

class AnswersRepository @Inject constructor(
    private val checkListPreUsoApi: CheckListPreUsoApi
): IAnswersRepository {
    override suspend fun saveAnswers(request: List<QuestionListResponse>): List<SaveAnswersResponse>{
        val response = checkListPreUsoApi.saveAnswers(request)
        val result = requireNotNull(response?.data)
        return result
    }
}
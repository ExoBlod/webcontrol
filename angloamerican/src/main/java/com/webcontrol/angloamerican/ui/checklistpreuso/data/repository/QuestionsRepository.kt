package com.webcontrol.angloamerican.ui.checklistpreuso.data.repository

import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.CheckListPreUsoApi
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.QuestionListByIdRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.QuestionListByIdResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.QuestionListResponse
import javax.inject.Inject

interface IQuestionsRepository{
    suspend fun getQuestions(request: String): List<QuestionListResponse>
    suspend fun getQuestionsById(request: QuestionListByIdRequest): List<QuestionListByIdResponse>
    suspend fun getQuestionsByIdhead(idHead: Int): List<QuestionListResponse>
}

class QuestionsRepository @Inject constructor(
    private val checkListPreUsoApi: CheckListPreUsoApi
): IQuestionsRepository {
    override suspend fun getQuestions(request: String): List<QuestionListResponse>{
        val response = checkListPreUsoApi.getQuestions(request)
        val result = requireNotNull(response?.data)
        return result
    }

    override suspend fun getQuestionsById(request: QuestionListByIdRequest): List<QuestionListByIdResponse>{
        val response = checkListPreUsoApi.getQuestionsById(request)
        val result = requireNotNull(response?.data)
        return result
    }

    override suspend fun getQuestionsByIdhead(idHead: Int): List<QuestionListResponse> {
        val response = checkListPreUsoApi.getQuestionsByIdHead(idHead)
        val result = requireNotNull(response?.data)
        return result
    }
}
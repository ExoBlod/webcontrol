package com.webcontrol.angloamerican.ui.checklistpreuso.views.listchecklist

import com.webcontrol.angloamerican.ui.checklistpreuso.data.NewCheckListQuestion
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.QuestionListResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.SaveAnswersResponse

sealed class ListChecklistUIEvent{
    data class Success(val questionListResponse: List<QuestionListResponse>) : ListChecklistUIEvent()
    data class SuccessAnswerSaving(val listSaveAnswersResponse: List<SaveAnswersResponse>) : ListChecklistUIEvent()
    data class Error(val error: Throwable) : ListChecklistUIEvent()
    object ShowLoading : ListChecklistUIEvent()
    object HideLoading : ListChecklistUIEvent()
}

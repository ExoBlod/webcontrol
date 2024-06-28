package com.webcontrol.angloamerican.ui.checklistpreuso.views.history

import com.webcontrol.angloamerican.ui.checklistpreuso.data.NewCheckListGroup
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.HistoryResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.QuestionListResponse

sealed class HistoryCheckListUIEvent{
    data class Success(val listHistory: List<HistoryResponse>) : HistoryCheckListUIEvent()
    data class SuccessSearchByCheckingHeadId(val questionListResponse: List<QuestionListResponse>) : HistoryCheckListUIEvent()
    object Error : HistoryCheckListUIEvent()
    data class ErrorString(val message: String) : HistoryCheckListUIEvent()
    object ShowLoading : HistoryCheckListUIEvent()
    object HideLoading : HistoryCheckListUIEvent()
}

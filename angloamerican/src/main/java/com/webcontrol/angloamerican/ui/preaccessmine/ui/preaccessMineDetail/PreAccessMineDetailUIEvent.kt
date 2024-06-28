package com.webcontrol.angloamerican.ui.preaccessmine.ui.preaccessMineDetail

import com.webcontrol.angloamerican.data.network.response.HistoryBookCourseData

sealed class PreAccessMineDetailUIEvent{
    data class Success(val listHistory: List<HistoryBookCourseData>) : PreAccessMineDetailUIEvent()
    data class SuccessSearchByCheckingHead(val listHistory: List<String>) : PreAccessMineDetailUIEvent()
    object Error : PreAccessMineDetailUIEvent()
    object ShowLoading : PreAccessMineDetailUIEvent()
    object HideLoading : PreAccessMineDetailUIEvent()
}
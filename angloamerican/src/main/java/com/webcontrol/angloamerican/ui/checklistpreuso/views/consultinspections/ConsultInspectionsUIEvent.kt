package com.webcontrol.angloamerican.ui.checklistpreuso.views.consultinspections

import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.HistoryByPlateResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.HistoryByWorkerIdResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.HistoryResponse


sealed class ConsultInspectionsUIEvent{
    data class Success(val listHistory: List<HistoryResponse>) : ConsultInspectionsUIEvent()
    object Error : ConsultInspectionsUIEvent()
    data class ErrorLog(val message: String) : ConsultInspectionsUIEvent()
    object ShowLoading : ConsultInspectionsUIEvent()
    object HideLoading : ConsultInspectionsUIEvent()
}

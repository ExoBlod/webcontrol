package com.webcontrol.angloamerican.ui.checklistpreuso.views.inputdata

import com.webcontrol.angloamerican.ui.checklistpreuso.data.AnswerCheckingHead
import com.webcontrol.angloamerican.ui.checklistpreuso.data.VehicleInformation
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.InsertInspectionResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.VehicleInformationResponse

sealed class InputDataUIEvent {
    data class Success(val listVehicleInformationResponse: List<VehicleInformationResponse>) : InputDataUIEvent()
    data class SuccessValidate(val answerCheckingHead: List<InsertInspectionResponse>) : InputDataUIEvent()
    object Error : InputDataUIEvent()
    object ShowLoading : InputDataUIEvent()
    object HideLoading : InputDataUIEvent()
}


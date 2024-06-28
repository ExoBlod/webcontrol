package com.webcontrol.android.ui.newchecklist.views.inputdata

import com.webcontrol.android.ui.newchecklist.data.AnswerCheckingHead
import com.webcontrol.android.ui.newchecklist.data.VehicleInformation

sealed class InputDataUIEvent {
    data class Success(val listVehicleInformation: VehicleInformation) : InputDataUIEvent()
    data class SuccessValidate(val answerCheckingHead: AnswerCheckingHead) : InputDataUIEvent()
    object Error : InputDataUIEvent()
    object ShowLoading : InputDataUIEvent()
    object HideLoading : InputDataUIEvent()
}


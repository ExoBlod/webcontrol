package com.webcontrol.angloamerican.ui.preaccessmine.ui.preaccessMineHistory

import com.webcontrol.angloamerican.data.db.entity.PreaccesoMina
import com.webcontrol.angloamerican.ui.preaccessmine.ui.preaccessMinePassengers.PreAccessMinePassengersUIEvent

sealed class PreAccessMineHistoryUIEvent{
    data class Success(val listHistory: List<PreaccesoMina>) : PreAccessMineHistoryUIEvent()
    data class SuccessSearchByCheckingHead(val listHistory: List<String>) : PreAccessMineHistoryUIEvent()
    data class  Error(val message: String) : PreAccessMineHistoryUIEvent()
    data class SuccessValidation(val validate: Boolean) : PreAccessMineHistoryUIEvent()
    object ShowLoading : PreAccessMineHistoryUIEvent()
    object HideLoading : PreAccessMineHistoryUIEvent()
}
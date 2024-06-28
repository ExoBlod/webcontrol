package com.webcontrol.angloamerican.ui.preaccessmine.ui.preaccessMinePassengers

import com.webcontrol.angloamerican.data.db.entity.PreaccesoDetalleMina
import com.webcontrol.angloamerican.data.dto.RegControl
import com.webcontrol.angloamerican.data.model.WorkerAnglo
import com.webcontrol.angloamerican.data.network.response.HistoryBookCourseData
import com.webcontrol.angloamerican.ui.preaccessmine.ui.newPreaccesMine.NewPreAccessMineUIEvent

sealed class PreAccessMinePassengersUIEvent{
    data class  Error(val message: String) : PreAccessMinePassengersUIEvent()
    object ShowLoading : PreAccessMinePassengersUIEvent()
    object HideLoading : PreAccessMinePassengersUIEvent()
    data class SuccessWorkerInfo(val workerInfo: WorkerAnglo) : PreAccessMinePassengersUIEvent()
    data class SuccessInsertPreaccessDetailMine(val idPreAccessMine: Long) : PreAccessMinePassengersUIEvent()
    data class SuccessValidation(val validate: Boolean) : PreAccessMinePassengersUIEvent()
}
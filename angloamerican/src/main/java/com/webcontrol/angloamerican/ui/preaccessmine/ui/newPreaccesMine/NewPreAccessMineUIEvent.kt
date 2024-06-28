package com.webcontrol.angloamerican.ui.preaccessmine.ui.newPreaccesMine

import com.webcontrol.angloamerican.data.db.entity.CheckListTest
import com.webcontrol.angloamerican.data.model.Division
import com.webcontrol.angloamerican.data.model.Local
import com.webcontrol.angloamerican.data.model.Vehiculo
import com.webcontrol.angloamerican.data.model.WorkerAnglo
import com.webcontrol.angloamerican.data.network.response.HistoryBookCourseData

sealed class NewPreAccessMineUIEvent{
    data class Success(val message: String) : NewPreAccessMineUIEvent()
    data class SuccessCheckList(val pair: Pair<NewPreAccessMineFragment.Companion.ChecklistType, CheckListTest>) : NewPreAccessMineUIEvent()
    data class SuccessDivision(val listDivision: List<Division>) : NewPreAccessMineUIEvent()
    data class SuccessLocal(val listLocal: List<Local>) : NewPreAccessMineUIEvent()
    data class SuccessVehicle(val vehicle: Vehiculo) : NewPreAccessMineUIEvent()
    data class SuccessInsertPreAcceso(val id: Long) : NewPreAccessMineUIEvent()
    data class  Error(val message: String) : NewPreAccessMineUIEvent()
    data class SuccessWorkerInfo(val workerInfo: WorkerAnglo) : NewPreAccessMineUIEvent()
    data class SuccessValidation(var validate: Boolean) : NewPreAccessMineUIEvent()
    object ShowLoading : NewPreAccessMineUIEvent()
    object HideLoading : NewPreAccessMineUIEvent()
}
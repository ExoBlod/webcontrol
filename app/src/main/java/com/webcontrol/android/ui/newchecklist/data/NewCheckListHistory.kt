package com.webcontrol.android.ui.newchecklist.data

import com.webcontrol.android.data.db.entity.NewCheckListHistorys

data class NewCheckListHistory(
    val checklistDate: String,
    val checklistInstanceId: Int,
    val checklistName: String,
    val checklistRight: Int = 0,
    val placa: String?="SIN PLACA",
    val validado: Int = 0,
    val vehiculoId: String,
    val workerId: String,
    val workerName: String,
    var status: String?="E1"
){
    fun toMapper():NewCheckListHistorys{
        return NewCheckListHistorys(
             workerId = this.workerId,
            workerName = this.workerName,
            vehiculoId = this.vehiculoId,
            validado = this.validado,
            checklistRight = this.checklistRight,
            checklistName = this.checklistName,
            checklistInstanceId = this.checklistInstanceId,
            placa = this.placa,
            checklistDate = this.checklistDate
        )
    }
}
package com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto

import com.google.gson.annotations.SerializedName
import com.webcontrol.angloamerican.ui.checklistpreuso.data.model.InspectionHistory

data class HistoryResponse(
    @SerializedName(value = "ChecklistName")
    var checklistName: String,

    @SerializedName(value = "WorkerName")
    var workerName: String,

    @SerializedName(value = "CheckingInHead")
    var checkingInHead: Int,

    @SerializedName(value = "Plate")
    var plate: String,

    @SerializedName(value = "WorkerId")
    var workerId: String,

    @SerializedName(value = "ChecklistDate")
    var checklistDate: String,

    @SerializedName(value = "Validate")
    var validate: Int,

    @SerializedName(value = "ChecklistRight")
    var checklistRight: Int,

    @SerializedName(value = "ChecklistStatus")
    var checklistStatus: String,
){
    fun toMapper(
    ): InspectionHistory {
        return InspectionHistory(
            workerId = this.workerId,
            plate = this.plate,
            checklistDate = this.checklistDate,
            checklistName = this.checklistName,
            checklistRight = this.checklistRight,
            validate = this.validate,
            workerName = this.workerName,
            checklistStatus = "E1",
            checkingHead = this.checkingInHead
        )
    }
}

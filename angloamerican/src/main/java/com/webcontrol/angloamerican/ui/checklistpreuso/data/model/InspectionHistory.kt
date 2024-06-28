package com.webcontrol.angloamerican.ui.checklistpreuso.data.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.HistoryResponse

@Entity(tableName = "inspection_history")
class InspectionHistory (
    @NonNull
    @ColumnInfo(name="checklist_name")
    var checklistName: String,

    @NonNull
    @ColumnInfo(name="worker_name")
    var workerName: String,

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "checking_head")
    var checkingHead: Int,

    @ColumnInfo(name="plate")
    var plate: String?="SIN PLACA",

    @NonNull
    @ColumnInfo(name="worker_id")
    var workerId: String,

    @NonNull
    @ColumnInfo(name="checklist_date")
    var checklistDate: String,

    @NonNull
    @ColumnInfo(name="validate")
    var validate: Int = 0,

    @NonNull
    @ColumnInfo(name="checklist_right")
    var checklistRight: Int = 0,

    @NonNull
    @ColumnInfo(name = "checklist_status")
    var checklistStatus: String,
){
    fun toMapper(
    ): HistoryResponse {
        return HistoryResponse(
            workerId = this.workerId,
            plate = this.plate ?: "",
            checklistDate = this.checklistDate,
            checklistName = this.checklistName,
            checklistRight = this.checklistRight,
            validate = this.validate,
            workerName = this.workerName,
            checklistStatus = "E1",
            checkingInHead = this.checkingHead
        )
    }
}



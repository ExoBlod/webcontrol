package com.webcontrol.android.data.db.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.webcontrol.android.ui.newchecklist.data.NewCheckListHistory
import com.webcontrol.android.ui.newchecklist.data.NewCheckListQuestion

@Entity(tableName = "new_checklist_history")
class NewCheckListHistorys (
    @NonNull
    @ColumnInfo(name="check|ist_Date")
    var checklistDate: String,

    @PrimaryKey
    @NonNull
    @ColumnInfo(name="checklist_Instance_Id")
    var checklistInstanceId: Int,

    @NonNull
    @ColumnInfo(name="checklist_Name")
    var checklistName: String,

    @NonNull
    @ColumnInfo(name="checklist_Right")
    var checklistRight: Int = 0,

    @ColumnInfo(name="placa")
    var placa: String?="SIN PLACA",

    @NonNull
    @ColumnInfo(name="validado")
    var validado: Int = 0,

    @NonNull
    @ColumnInfo(name="vehiculoId")
    var vehiculoId: String,

    @NonNull
    @ColumnInfo(name="workerId")
    var workerId: String,

    @NonNull
    @ColumnInfo(name="workerName")
    var workerName: String
        ){
    fun toMapper(
    ): NewCheckListHistory {
        return NewCheckListHistory(
            workerId = this.workerId,
            placa = this.placa,
            checklistDate = this.checklistDate,
            checklistInstanceId = this.checklistInstanceId,
            checklistName = this.checklistName,
            checklistRight = this.checklistRight,
            validado = this.validado,
            vehiculoId = this.vehiculoId,
            workerName = this.workerName,
            status = "E1"
        )
    }
}


package com.webcontrol.angloamerican.data.db.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

import com.google.gson.annotations.SerializedName

@Entity(
        tableName = "checklist_group",
        indices = [Index(value = ["idDb", "id_tipo", "id_check", "id_checkgroup"], unique = true)]
        )
class ChecklistGroups {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    var id :Int = 0

    @NonNull
    var idDb :Int = 0

    @ColumnInfo(name = "id_tipo")
    var checklistTypeId: String? = ""

    @ColumnInfo(name = "id_check")
    var checklistId :Int  = 0

    @ColumnInfo(name = "id_checkgroup")
    @SerializedName(value = "GroupId", alternate = ["groupId"])
    var checklistGroupId :Int  = 0

    @ColumnInfo(name = "nombre")
    @SerializedName(value = "GroupName", alternate = ["groupName"])
    var groupName: String? = ""

    @ColumnInfo(name = "tipo")
    @SerializedName(value = "GroupType", alternate = ["groupType"])
    var groupType: String? = ""

    @ColumnInfo(name = "mensaje")
    @SerializedName(value = "GroupMessage", alternate = ["groupMessage"])
    var groupMessage: String? = ""

    @Ignore
    @SerializedName(value = "Questions", alternate = ["questions"])
    var preguntas: List<CheckListTest_Detalle> = ArrayList()

}

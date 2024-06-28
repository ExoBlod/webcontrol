package com.webcontrol.android.data.db.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "credencial", primaryKeys = ["id"])
class WorkerCredencial (
    @NonNull
    @SerializedName("id")
    @ColumnInfo(name="id")
    var id: Int,

    @NonNull
    @SerializedName("PersonId")
    @ColumnInfo(name="Person_Id")
    var personId: Int,

    @SerializedName("WorkerId")
    @ColumnInfo(name="Worker_Id")
    val workerId: String? = "",

    @SerializedName("SupervisorName")
    @ColumnInfo(name="Supervisor_Name")
    val supervisorName: String? = "",

    @SerializedName("SupervisorLastName")
    @ColumnInfo(name="Supervisor_Last_Name")
    val supervisorLastName: String? = "",

    @SerializedName("IsSignature")
    @ColumnInfo(name="Is_Signature")
    val isSignature: Int? =0
        )
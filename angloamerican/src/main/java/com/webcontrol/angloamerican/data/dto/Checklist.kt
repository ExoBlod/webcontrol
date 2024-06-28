package com.webcontrol.angloamerican.data.dto

import com.google.gson.annotations.SerializedName

data class Checklist(
    @SerializedName("ChecklistInstanceId")
    val id: Int,
    @SerializedName("ChecklistName")
    val name: String,
    @SerializedName("VehiculoId")
    val vehicleId: String,
    @SerializedName("WorkerId")
    val workerId: String,
    @SerializedName("ChecklistDate")
    val date: String,
)

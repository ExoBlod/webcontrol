package com.webcontrol.android.data.model

import androidx.annotation.NonNull
import com.google.gson.annotations.SerializedName

class Checklists(
    @NonNull
    @SerializedName(value = "ChecklistId", alternate = ["checklistId"])
    var id: String,
    @SerializedName(value = "ChecklistTypeId", alternate = ["checklistTypeId"])
    var tipoId: String,
    @SerializedName(value = "ChecklistName", alternate = ["checklistName"])
    var nombre: String
) {
    @NonNull
    @Override
    override fun toString(): String {
        return nombre
    }
}
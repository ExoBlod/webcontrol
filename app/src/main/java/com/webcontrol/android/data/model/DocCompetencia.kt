package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class DocCompetencia (
        @SerializedName("IdDoc")
        var idDoc : Int,
        @SerializedName("Nombre")
        val nombre: String = ""
){
}
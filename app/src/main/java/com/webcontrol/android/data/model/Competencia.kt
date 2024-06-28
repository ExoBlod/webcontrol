package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class Competencia (
        @SerializedName("IdRol")
        var idRol : Int,
        @SerializedName("Descripcion")
        val descripcion: String = "",
        @SerializedName("Documentos")
        val documentos: List<DocCompetencia>? = null
){
}
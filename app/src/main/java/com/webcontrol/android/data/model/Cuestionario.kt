package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class Cuestionario(
    @SerializedName("Codigo")
    val codigo: Int,
    @SerializedName("CodFormato")
    val codFormato: String?,
    @SerializedName("Descripcion")
    val descripcion: String?,
    @SerializedName("Orden")
    val orden: String?,
    @SerializedName("Tipo")
    val tipo: String?,
    @SerializedName("Alternativas")
    val alternativas: ArrayList<Alternativa>?
)
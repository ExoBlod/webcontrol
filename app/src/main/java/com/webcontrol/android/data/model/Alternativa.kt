package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class Alternativa(
    @SerializedName("Codigo")
    val codigo: Int,
    @SerializedName("CodCuestionario")
    val codCuestionario: Int,
    @SerializedName("Descripcion")
    val descripcion: String?,
    @SerializedName("Comenta")
    val comenta: Boolean,
    @SerializedName("Tipo")
    val tipo: String?,
    @SerializedName("Orden")
    val orden: String?
)
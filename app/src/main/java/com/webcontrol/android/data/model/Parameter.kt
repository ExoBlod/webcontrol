package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class Parameter(
    @SerializedName("Id_Parametro")
    val id: Int,
    @SerializedName("Descripcion")
    val description: String,
    @SerializedName("Titulo1")
    val title: String,
    @SerializedName("Valor1")
    val value: String
) {
}
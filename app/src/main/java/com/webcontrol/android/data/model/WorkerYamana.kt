package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class WorkerYamana (
        @SerializedName("flicconducir")
        var flicconducir: String = "",
        @SerializedName("fpsicosensotecnico")
        var fpsicosensotecnico: String = "",
        @SerializedName("fmanejodef")
        var fmanejodef: String = "",
        @SerializedName("fingreso")
        var fingreso: String = "",
        @SerializedName("rol")
        var rol: String = "",
        @SerializedName("autorizado")
        var autorizado: String = "",
        @SerializedName("rut")
        var rut: String = "",
        @SerializedName("apellidos")
        var apellidos: String = "",
        @SerializedName("nombres")
        var nombres: String = "",
        @SerializedName("empresa")
        var empresa: String = "",
        @SerializedName("mandante")
        var mandante: String = "",
        @SerializedName("foto")
        var foto: String = ""

){
}
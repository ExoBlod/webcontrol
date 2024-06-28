package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class WorkerCaserones (
        @SerializedName("FLICCONDUCIR")
        var FLICCONDUCIR: String = "",
        @SerializedName("FPSICOSENSOTECNICO")
        var FPSICOSENSOTECNICO: String = "",
        @SerializedName("FINGRESO")
        var FINGRESO: String = "",
        @SerializedName("MUNICIPAL")
        var MUNICIPAL: String = "",
        @SerializedName("ZONA")
        var ZONA: String = "",
        @SerializedName("TIPOVEHICULO")
        var TIPOVEHICULO: String = "",
        @SerializedName("FOTO")
        var FOTO: String = "",
        @SerializedName("ROL")
        var ROL: String = "",
        @SerializedName("AUTORIZADO")
        var AUTORIZADO: String = "",
        @SerializedName("RUT")
        var rut: String = "",
        @SerializedName("APELLIDOS")
        var apellidos: String = "",
        @SerializedName("NOMBRES")
        var nombres: String = "",
        @SerializedName("EMPRESA")
        var empresa: String = ""

){
}
package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class WorkerCredentialBarrick (
        @SerializedName("RUT")
        var workerId : String = "",
        @SerializedName("NOMBRES")
        val nombres: String = "",
        @SerializedName("APELLIDOS")
        val apellidos: String = "",
        @SerializedName("ROL")
        val rol: String = "",
        @SerializedName("FINGRESO")
        val fechain: String = "",
        @SerializedName("FLICCONDUCIR")
        val fechalic: String = "",
        @SerializedName("FPSICOSENSOTECNICO")
        val fechasico: String = "",
        @SerializedName("Clase")
        var clase: String?= "NA",
        @SerializedName("EMPRESA")
        val empresa: String = "",
        @SerializedName("AUTORIZADO")
        val autorizacion: String = "",
        @SerializedName("Mandante")
        val mandante: String = "",
        @SerializedName("ZonasAcceso")
        val zonasAcceso: String? = "",
        @SerializedName("TIPOVEHICULO")
        val tipoVehiculos: String? = "",
        @SerializedName("ZonasConduce")
        val zonasConduce: String? = "",
        @SerializedName("FOTO")
        var foto: String = "",
        @SerializedName("DivisionId")
        var divisonId: String? = "",
        @SerializedName("MUNICIPAL")
        var municipal: String? = "",
        @SerializedName("PAIS")
        var pais: String? = ""
) {
}
package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class WorkerKinrossCredencial (
        @SerializedName("WorkerId")
        var workerId : String = "",
        @SerializedName("Nombre")
        val nombres: String = "",
        @SerializedName("Apellidos")
        val apellidos: String = "",
        @SerializedName("Rol")
        val rol: String = "",
        @SerializedName("FechaIn")
        val fechain: String = "",
        @SerializedName("FechaLic")
        val fechalic: String = "",
        @SerializedName("FechaSico")
        val fechasico: String = "",
        @SerializedName("Clase")
        var clase: String?= "NA",
        @SerializedName("Empresa")
        val empresa: String = "",
        @SerializedName("Autorizacion")
        val autorizacion: String = "",
        @SerializedName("Mandante")
        val mandante: String = "",
        @SerializedName("ZonasAcceso")
        val zonasAcceso: String? = "",
        @SerializedName("TipoVehiculo")
        val tipoVehiculos: String? = "",
        @SerializedName("ZonasConduce")
        val zonasConduce: String? = "",
        @SerializedName("Foto")
        var foto: String = "",
        @SerializedName("DivisionId")
        var divisonId: String? = ""
) {
}
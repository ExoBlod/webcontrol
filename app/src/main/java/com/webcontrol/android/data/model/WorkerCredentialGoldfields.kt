package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class WorkerCredentialGoldfields (
        @SerializedName("RUT")
        val workerId : String = "",
        @SerializedName("NOMBRES")
        val nombres: String = "",
        @SerializedName("APELLIDOS")
        val apellidos: String = "",
        @SerializedName("ROL")
        val rol: String = "",
        @SerializedName("FINGRESO")
        val fechain: String? = "",
        @SerializedName("FLICCONDUCIR")
        val fechalic: String? = "",
        @SerializedName("FPSICOSENSOTECNICO")
        val fechasico: String? = "",
        @SerializedName("MUNICIPAL")
        var licMunicipal: String?= "",
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
        val foto: String? = "",
        @SerializedName("DivisionId")
        val divisionId: String? = ""
)
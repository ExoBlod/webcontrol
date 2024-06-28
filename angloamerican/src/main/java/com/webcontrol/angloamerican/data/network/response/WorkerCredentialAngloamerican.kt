package com.webcontrol.angloamerican.data.network.response

import com.google.gson.annotations.SerializedName

class WorkerCredentialAngloamerican(
    @SerializedName("RUT")
    var workerId : String = "",

    @SerializedName("AUTORIZADO")
    var workerAutorizado : String = "",

    @SerializedName("AREA")
    var area : String = "",

    @SerializedName("APELLIDOS")
    var workerApellidos : String = "",

    @SerializedName("NOMBRES")
    var workerNombres : String = "",

    @SerializedName("EMPRESA")
    var workerEmpresa : String = "",

    @SerializedName("FLICCONDUCIR")
    var workerFlicconducir : String = "",

    @SerializedName("FPSICOSENSOTECNICO")
    var workerPsico : String = "",

    @SerializedName("FOTO")
    var foto: String = "",

    @SerializedName("Mandante")
    val mandante: String = "", 

    @SerializedName("ROL")
    val rol: String = "",

    @SerializedName("ZonasAcceso")
    val zonasAcceso: String? = "",

    @SerializedName("MUNICIPAL")
    var clase: String?= "NA",

    @SerializedName("FINGRESO")
    val fechain: String = "",

    @SerializedName("TIPOVEHICULO")
    val tipoVehiculos: String? = "",

    @SerializedName("ZonasConduce")
    val zonasConduce: String? = "",

    @SerializedName("ExplorkFech")
    val explorkFech: String? = "",

    @SerializedName("LicenciaExActiva")
    val licenciaExActiva: String? = "",

    @SerializedName("FechaEntregaEx")
    val fechaEntregaEx: String? = "",

    @SerializedName("Restricciones")
    val restricciones: String? = "",
)
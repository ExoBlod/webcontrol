package com.webcontrol.pucobre.data.model

import com.google.gson.annotations.SerializedName

data class WorkerCredentialPucobre (
    @SerializedName("RUT")
    var workerId : String = "",

    @SerializedName("AUTORIZADO")
    var workerAutorizado : String = "",

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

    @SerializedName("MANDANTE")
    val mandante: String = "",

    @SerializedName("ROL")
    val rol: String = "",

    @SerializedName("ZonasAcceso")
    val zonasAcceso: String? = "",

    @SerializedName("Clase")
    var clase: String?= "NA",

    @SerializedName("FINGRESO")
    val fechain: String = "",

    @SerializedName("TIPOVEHICULO")
    val tipoVehiculos: String? = "",

    @SerializedName("ZonasConduce")
    val zonasConduce: String? = "",

    @SerializedName("SUPERINTENDENCIA")
    val workerSuperintendencia: String? = "",

    @SerializedName("DEPARTAMENTO")
    var workerDepartamento: String?= "",

    @SerializedName("CATEGORIA")
    val workerCategoria: String = "",

    @SerializedName("GERENCIAS")
    val workerGerencias: String? = "",

    @SerializedName("ESCALA")
    val workerEscala: String? = "",

    @SerializedName("FAENA")
    val workerFaena: String? = "",

    @SerializedName("GSANGRE")
    val gsangre: String? = "",

    @SerializedName("DIRECCION")
    val workerDireccion: String? = "",

    @SerializedName("FONO")
    val workerTelefono: String? = "",

    @SerializedName("MUNICIPAL")
    val municipal: String? = "",

    @SerializedName("CONDUCTOR")
    val conductor: String? = "",

    @SerializedName("ISAUTH")
    val autorizacionLector: Boolean = false,

    val superficie: String? = "",

    val subterranea: String? = "",

    val planta: String? = ""
    )
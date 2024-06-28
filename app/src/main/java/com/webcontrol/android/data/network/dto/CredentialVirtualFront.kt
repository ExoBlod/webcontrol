package com.webcontrol.android.data.network.dto

import com.google.gson.annotations.SerializedName
import com.webcontrol.angloamerican.data.*

class CredentialVirtualFront(
    @SerializedName("RUT")
    var rut: String,
    @SerializedName("EMPRESA")
    var empresa: String,
    @SerializedName("NOMBRES")
    var nombres: String,
    @SerializedName("APELLIDOS")
    var apellidos: String,
    @SerializedName("FOTO")
    var foto: String?,
    @SerializedName("GERENCIA")
    var gerencia: String,
    @SerializedName("AREA")
    var area: String,
    @SerializedName("CARGO")
    var cargo: String,
    @SerializedName("AUTORIZADO_ACCESO")
    var autorizadoAcceso: String,
    @SerializedName("AUTORIZADO_CONDUCTOR")
    var autorizadoConductor: String,
)
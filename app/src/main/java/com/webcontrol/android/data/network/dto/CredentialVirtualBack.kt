package com.webcontrol.android.data.network.dto

import com.google.gson.annotations.SerializedName
import com.webcontrol.angloamerican.data.*

class CredentialVirtualBack(
    @SerializedName("CONTRATISTA")
    var contratista: String,
    @SerializedName("SUBCONTRATISTA")
    var subcontratista: String,
    @SerializedName("CONTRATO")
    var contrato: String,
    @SerializedName("VIGENCIA")
    var vigencia: String,
    @SerializedName("NROLICENCIA")
    var nroLicencia: String,
    @SerializedName("CATEGORIAS")
    var categorias: String,
    @SerializedName("USALENTES")
    var usaLentes: String,
    @SerializedName("DOCUMENTOS_ACCESOS")
    var docAccess: List<CredentialVirtualDocuments>?,
    @SerializedName("DOCUMENTOS_CONDUCTOR")
    var docDriver: List<CredentialVirtualDocuments>?,
)
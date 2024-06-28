package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class WorkerLaPoderosa(

    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("identificador")
    val identificador: String?,
    @SerializedName("nombres")
    val nombres: String?,
    @SerializedName("apellidos")
    val apellidos: String?,
    @SerializedName("empresaContratista")
    val empresaContratista: String?,
    @SerializedName("empresaSubContratista")
    val empresaSubContratista: String?,
    @SerializedName("cargo")
    val cargo: String?,
    @SerializedName("tipoPase")
    val tipoPase: String?,
    @SerializedName("nroContrato")
    val nroContrato: String?,
    @SerializedName("autorizadoAcceso")
    val autorizadoAcceso: String?,
    @SerializedName("autorizadoConducir")
    val autorizadoConducir: String?,
    @SerializedName("gerencia")
    val gerencia: String?,
    @SerializedName("area")
    val area: String?,
    @SerializedName("nroLicencia")
    val nroLicencia: String?,
    @SerializedName("zonasCond")
    val zonasCond: String?,
    @SerializedName("tiposVehi")
    val tiposVehi: String?,
    @SerializedName("usaLentes")
    val usaLentes: String?,
    @SerializedName("categoria")
    val categoria: String?,
    @SerializedName("vigenciaAutorizado")
    val vigenciaAutorizado: String?,
    @SerializedName("foto")
    val foto: String?,
    @SerializedName("mandante")
    val mandante: String?,
    @SerializedName("acreditado")
    val acreditado: String?,
    @SerializedName("idSync")
    val idsync: Long = 0,
) {
}

package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class RequestReservaBus (
    @SerializedName("WorkerId")
    val workerId: String?=null,

    @SerializedName("Date")
    val date: String? = null,

    @SerializedName("SyncId")
    val syncId: Int? = null,

    @SerializedName("Origen")
    val source: String? = null,

    @SerializedName("Destino")
    val destiny: String? = null,

    @SerializedName("FechaIda")
    val departureDate: String? = null,

    @SerializedName("FechaVuelta")
    val returnDate: String? = null,

    @SerializedName("Division")
    val divition: String? = null,

    @SerializedName("Rut")
    val rut: String? = null,

    @SerializedName("Usuario")
    val usuario: String? = null,

    @SerializedName("PageSize")
    val pageSize: Int? = null,

    @SerializedName("DivisionId")
    val divisionId: String? = null,

    @SerializedName("Fecha")
    val fecha: String? = null,

    @SerializedName("DireccionTrip")
    val typeTrip: String? = null,

    @SerializedName("IdProg")
    val idProg: Long? = null,

    @SerializedName("Patente")
    val patente: String? = null,


    @SerializedName("IdProgIda")
    val idProgIda: Long? = null,

    @SerializedName("IdProgVuelta")
    val idProgVuelta: Long? = null,

    @SerializedName("Empresa")
    val empresa: String? = null,

    @SerializedName("Ost")
    val ost: String? = null,

    @SerializedName("AsientoIda")
    val asientoIda: Int? = null,

    @SerializedName("AsientoVuelta")
    val asientoVuelta: Int? = null

) {
     fun toString1(): String {
        return "RequestReservaBus(source=$source, destiny=$destiny, divisionId=$divisionId, fecha=$fecha, typeTrip=$typeTrip)"
    }

    fun toString2(): String {
        return "RequestReservaBus(rut=$rut, idProgIda=$idProgIda, idProgVuelta=$idProgVuelta, asientoIda=$asientoIda, asientoVuelta=$asientoVuelta)"
    }
}
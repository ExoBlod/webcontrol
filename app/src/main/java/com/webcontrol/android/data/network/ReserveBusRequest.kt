package com.webcontrol.android.data.network

import com.google.gson.annotations.SerializedName

class ReserveBusRequest (
    val Codigo: Int? = null,
    val Rut: String? = null,
    val Empresa: String? = null,
    val Whomake: String? = null,
    @SerializedName("DivisionId")
    val divisionId: String? = null,
    @SerializedName("Origen")
    val source: String? = null,
    @SerializedName("Destino")
    val destiny: String? = null,
    @SerializedName("DireccionTrip")
    val typeTrip: String? = null,
    @SerializedName("Fecha")
    val fecha: String? = null,
)
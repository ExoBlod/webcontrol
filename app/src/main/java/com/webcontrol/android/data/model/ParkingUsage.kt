package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class ParkingUsage(
    @SerializedName("Rut")
    val rut: String,
    @SerializedName("Patente")
    val patent: String,
    @SerializedName("Fecha")
    val date: String,
    @SerializedName("Division")
    val divisionId: String,
    @SerializedName("Local")
    val localId: String,
    @SerializedName("Sentido")
    val direction: String
) {
}
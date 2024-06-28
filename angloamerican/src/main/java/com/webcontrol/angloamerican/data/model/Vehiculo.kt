package com.webcontrol.angloamerican.data.model

import com.google.gson.annotations.SerializedName

data class Vehiculo(
    @SerializedName("VehicleId")
    var id: String = "",

    @SerializedName("IsAuthorized")
    val isAutor: Boolean = false,

    @SerializedName("VehicleType")
    val vehicleType: String = "",

    @SerializedName("Licencias")
    val licencias: String = ""
)
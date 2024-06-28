package com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto

import com.google.gson.annotations.SerializedName

data class VehicleInformationResponse (
    @SerializedName(value = "Plate")
    var plate: String = "",

    @SerializedName(value = "Brand")
    var brand: String = "",

    @SerializedName(value = "Model")
    var model: String = "",

    @SerializedName(value = "IdTypeVehicle")
    var idTypeVehicle: Int = 0,

    @SerializedName(value = "TypeVehicle")
    var typeVehicle: String =  "",
)
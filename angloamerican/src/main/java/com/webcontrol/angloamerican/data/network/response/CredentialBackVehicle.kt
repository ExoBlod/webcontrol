package com.webcontrol.angloamerican.data.network.response

import com.google.gson.annotations.SerializedName
import com.webcontrol.angloamerican.data.VEHICLE_TYPE

class CredentialBackVehicle(
    @SerializedName(VEHICLE_TYPE)
    var vehicleType: String = ""
)
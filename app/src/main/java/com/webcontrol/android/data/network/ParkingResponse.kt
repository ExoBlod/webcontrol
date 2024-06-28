package com.webcontrol.android.data.network

import com.google.gson.annotations.SerializedName

data class ParkingResponse(
    @SerializedName("Ocupados")
    val taken: Int
) {
}
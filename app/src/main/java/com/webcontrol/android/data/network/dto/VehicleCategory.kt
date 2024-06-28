package com.webcontrol.android.data.network.dto

data class VehicleCategory(
    val name: String,
    val vehicleList: List<VehicleType> = ArrayList()
)
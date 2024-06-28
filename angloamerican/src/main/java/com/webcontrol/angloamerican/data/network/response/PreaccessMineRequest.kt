package com.webcontrol.angloamerican.data.network.response

import com.google.gson.annotations.SerializedName
data class PreaccessMineRequest(
    @SerializedName("WorkerId")
    val WorkerId: String,
    @SerializedName("PreAccessDate")
    val PreAccessDate: String,
    @SerializedName("PreAccessTime")
    val PreAccessTime: String,
    @SerializedName("VehicleId")
    val VehicleId: String,
    @SerializedName("CompanyId")
    val CompanyId: String,
    @SerializedName("DivisionId")
    val DivisionId: String,
    @SerializedName("LocalId")
    val LocalId: String,
    @SerializedName("InOut")
    val InOut: String,
    @SerializedName("Ost")
    val Ost: String,
    @SerializedName("CostCenter")
    val CostCenter: String,
    @SerializedName("PassType")
    val PassType: String,
    @SerializedName("UserId")
    val UserId: String,
    @SerializedName("TripId")
    val TripId: String,
    @SerializedName("IsDriver")
    val IsDriver: String,
    @SerializedName("ErrorCode")
    val ErrorCode: String,
    @SerializedName("IdGuia")
    val IdGuia: String,
    @SerializedName("Passengers")
    val Passengers: String
)

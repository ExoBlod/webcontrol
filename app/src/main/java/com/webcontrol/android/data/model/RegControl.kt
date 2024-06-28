package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class RegControl(
        @SerializedName("WorkerId")
        var workerId: String,

        @SerializedName("PreAccessDate")
        var date: String,

        @SerializedName("PreAccessTime")
        var time: String,

        @SerializedName("VehicleId")
        var vehicleId: String,

        @SerializedName("CompanyId")
        var companyId: String,

        @SerializedName("DivisionId")
        var divisionId: String,

        @SerializedName("LocalId")
        var localId: String,

        @SerializedName("InOut")
        var inOut: String,

        @SerializedName("Ost")
        var ost: String,

        @SerializedName("CostCenter")
        var costCenter: String,

        @SerializedName("PassType")
        var passKind: String,

        @SerializedName("UserId")
        var userId: String,

        @SerializedName("TripId")
        var tripId: String,

        @SerializedName("IsDriver")
        var driver: String,

        @SerializedName("ErrorCode")
        var errorCode: String,

        @SerializedName("CodPda")
        var codPda: String = "ANDROID",

        @SerializedName("IdGuia")
        var idGuia: String = "",

        @SerializedName("Passengers")
        var passengers: String = ""
)
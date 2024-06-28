package com.webcontrol.android.data.network

import com.google.gson.annotations.SerializedName

data class AttendanceRequest (
    @SerializedName("WorkerId")
    val workerId: String,
    @SerializedName("MarkDate")
    val date: String,
    @SerializedName("MarkTime")
    val time: String,
    @SerializedName("DivisionId")
    val divisionId: String,
    @SerializedName("InOut")
    val inout: String,
    @SerializedName("Country")
    val country: String
)
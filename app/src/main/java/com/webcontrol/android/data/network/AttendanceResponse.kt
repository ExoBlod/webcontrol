package com.webcontrol.android.data.network

import com.google.gson.annotations.SerializedName

data class AttendanceResponse (
    @SerializedName("Status")
    val status: Boolean,
    @SerializedName("Result")
    val result: String?,
    @SerializedName("WorkerId")
    val workerId: String?,
    @SerializedName("MarkDate")
    val markDate: String?,
    @SerializedName("MarkTime")
    val markTime: String?,
    @SerializedName("InOut")
    val inout: String?
)
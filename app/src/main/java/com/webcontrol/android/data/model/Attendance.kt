package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class Attendance (
    @SerializedName("AssistId")
    val id: Int,
    @SerializedName("AssistDate")
    val date: String,
    @SerializedName("InOut")
    val inout: String,
    @SerializedName("Local")
    val local: String,
    @SerializedName("MarkTime")
    val time: String,
)
package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class WorkerLocation(
    @SerializedName("WorkerId")
    var workerId:String,
    @SerializedName("Date")
    var date: String,
    @SerializedName("Time")
    var time: String,
    @SerializedName("Latitude")
    var latitude: String?,
    @SerializedName("Longitude")
    var longitude: String?
)
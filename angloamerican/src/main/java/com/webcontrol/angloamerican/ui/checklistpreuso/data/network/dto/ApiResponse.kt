package com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("Success")
    var success: Boolean,
    @SerializedName("Message")
    var message: String,
    @SerializedName("Data")
    var data: T,
)
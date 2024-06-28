package com.webcontrol.angloamerican.data.network.response

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("Success", alternate = ["success"])
    var success: Boolean,
    @SerializedName("Message" , alternate = ["message"])
    var message: String,
    @SerializedName("Data", alternate = ["data"])
    var data: T,
)
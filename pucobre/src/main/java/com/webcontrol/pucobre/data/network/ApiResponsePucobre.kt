package com.webcontrol.pucobre.data.network

import com.google.gson.annotations.SerializedName

class ApiResponsePucobre <T>(
    @SerializedName(value = "success", alternate = ["Success"])
    var isSuccess: Boolean,

    @SerializedName(value = "message", alternate = ["Message"])
    var message: String,

    @SerializedName(value = "data", alternate = ["Data"])
    var data: T,

    var Status: Int
)
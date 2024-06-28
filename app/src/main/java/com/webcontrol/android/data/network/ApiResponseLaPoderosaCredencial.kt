package com.webcontrol.android.data.network

import com.google.gson.annotations.SerializedName

data class ApiResponseLaPoderosaCredencial<T>(
    @SerializedName(value = "success", alternate = ["Success"])
    var isSuccess: Boolean,

    @SerializedName(value = "message", alternate = ["Message"])
    var message: String,

    @SerializedName(value = "data", alternate = ["Data"])
    var data: T,

    var Status: Int
)

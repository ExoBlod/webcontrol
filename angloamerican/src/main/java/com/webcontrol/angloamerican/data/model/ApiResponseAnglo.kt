package com.webcontrol.angloamerican.data.model

import com.google.gson.annotations.SerializedName

data class ApiResponseAnglo<T>(

        @SerializedName(value = "success", alternate = ["Success"])
        var isSuccess: Boolean,

        @SerializedName(value = "message", alternate = ["Message"])
        var message: String,

        @SerializedName(value = "data", alternate = ["Data"])
        var data: T,

        var Status: Int
)
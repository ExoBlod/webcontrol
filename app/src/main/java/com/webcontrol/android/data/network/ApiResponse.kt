package com.webcontrol.android.data.network

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
        @SerializedName("success")
        var isSuccess: Boolean,
        var message: String,
        var data: T,
        var dataNested: List<T>
)
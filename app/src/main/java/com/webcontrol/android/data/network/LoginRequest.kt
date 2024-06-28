package com.webcontrol.android.data.network

import com.google.gson.annotations.SerializedName

data class LoginRequest (
    @SerializedName("WorkerId")
    val workerId: String
)
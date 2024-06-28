package com.webcontrol.pucobre.data.dto

import com.google.gson.annotations.SerializedName

data class TokenRequest(
    @SerializedName("WorkerId")
    val workerId: String
)
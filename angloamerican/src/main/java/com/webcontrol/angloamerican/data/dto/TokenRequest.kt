package com.webcontrol.angloamerican.data.dto

import com.google.gson.annotations.SerializedName

data class TokenRequest (
    @SerializedName("WorkerId")
    val workerId: String
)
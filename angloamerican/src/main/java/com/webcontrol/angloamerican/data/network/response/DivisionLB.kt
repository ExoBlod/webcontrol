package com.webcontrol.angloamerican.data.network.response

import com.google.gson.annotations.SerializedName
data class DivisionLB(
    @SerializedName("DivisionId")
    val DivisonId: String,
    @SerializedName("Name")
    val Name: String,
)

package com.webcontrol.angloamerican.data.network.response

import com.google.gson.annotations.SerializedName
data class LocalsAccess(
    @SerializedName("LocalId")
    val LocalId: String,
    @SerializedName("DivisionId")
    val DivisionId: String,
    @SerializedName("Description")
    val Description: String,
    @SerializedName("LocalType")
    val LocalType: String,
)

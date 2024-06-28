package com.webcontrol.angloamerican.data.network.response

import com.google.gson.annotations.SerializedName

data class AuthorizedDivisions(
    @SerializedName("RUT")
    val rut: String,

    @SerializedName("DIVISION")
    val division: String,
)

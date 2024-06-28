package com.webcontrol.angloamerican.data.network.response

import com.google.gson.annotations.SerializedName

data class AuthorizedAreas(
    @SerializedName("AREA")
    val authArea: String
)


package com.webcontrol.angloamerican.data.model

import com.google.gson.annotations.SerializedName

class DenyMovementResponse(
    @SerializedName("Result")
    val message: String
)
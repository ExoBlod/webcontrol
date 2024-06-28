package com.webcontrol.angloamerican.data.model

import com.google.gson.annotations.SerializedName

class DenyMovementRequest(
    @SerializedName("IDPASE")
    val idPass: Int,

    @SerializedName("USUARIOAPROBADOR")
    val approverUser: String,

    @SerializedName("MOTIVORECHAZO")
    val rejectReason: String,
)
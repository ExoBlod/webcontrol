package com.webcontrol.angloamerican.data.model

import com.google.gson.annotations.SerializedName

class ApproveMovementRequest(
    @SerializedName("IDPASE")
    val idPass: Int,

    @SerializedName("PATENTE")
    val plate: String,

    @SerializedName("DIVCOD")
    val divisionCode: String,

    @SerializedName("TIPOPASE")
    val passType: String,

    @SerializedName("LOTEFINICIO")
    val startDate: String,

    @SerializedName("LOTEFFINAL")
    val endDate: String,

    @SerializedName("USUARIOAPROBADOR")
    val approverUser: String
)
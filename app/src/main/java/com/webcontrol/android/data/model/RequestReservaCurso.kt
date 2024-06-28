package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName



data class RequestReservaCurso (
    @SerializedName("PageSize")
    val pageSize: Int,

    @SerializedName("Rut")
    val rut: String
)
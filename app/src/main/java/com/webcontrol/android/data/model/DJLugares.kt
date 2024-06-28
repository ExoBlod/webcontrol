package com.webcontrol.android.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DJLugares (
    @SerializedName("DescLugar")
    var descripcion: String = "",
    @SerializedName("FechaIn")
    var fechaIn: String = "",
    @SerializedName("FechaOut")
    var fechaOut: String = ""
): Parcelable
package com.webcontrol.android.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DJConsolidado(
    @SerializedName("ID")
    val id: Int,
    @SerializedName("RUT")
    val rut: String,
    @SerializedName("FECHA")
    val fecha: String
): Parcelable
package com.webcontrol.android.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DJRespuesta (
    @SerializedName("IdHito")
    var idHito: Int,
    @SerializedName("Rut")
    var rut: String,
    @SerializedName("Fecha")
    var fecha: String,
    @SerializedName("Resp")
    var respuesta: String,
    var tipo: String,
    var bloquea: Boolean = false,
    @SerializedName("DJLugares")
    var lugares: ArrayList<DJLugares>? = null
): Parcelable
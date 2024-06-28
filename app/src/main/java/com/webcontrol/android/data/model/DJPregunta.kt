package com.webcontrol.android.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DJPregunta(
        @SerializedName("ID")
        val id: Int,
        @SerializedName("DESCRIPCION")
        val descripcion: String?,
        @SerializedName("ORDEN")
        val orden: Int,
        @SerializedName("TIPO")
        val tipo: String?,
        @SerializedName("BLOQUEA")
        val bloquea: Boolean = false,
        @SerializedName("ALTERNATIVAS")
        val alternativas: String?
): Parcelable
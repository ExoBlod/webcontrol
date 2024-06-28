package com.webcontrol.android.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ControlCuarentena (
    @SerializedName("CODIGO")
    val codigo: Int,
    @SerializedName("CODCTRL_INICIAL")
    val codCtrlInicial: Int,
    @SerializedName("RUT")
    val rut: String,
    @SerializedName("FECHAINI")
    val fechaIni: String?,
    @SerializedName("FECHAFIN")
    val fechaFin: String?,
    @SerializedName("USUARIO")
    val usuario: String?,
    @SerializedName("F200")
    var f200: String?,
    @SerializedName("VALF200")
    val valf200: String?
): Parcelable
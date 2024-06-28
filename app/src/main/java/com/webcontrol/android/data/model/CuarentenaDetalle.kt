package com.webcontrol.android.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CuarentenaDetalle (
        @SerializedName("CodCuarentena")
        var codCuarentena: Int,
        @SerializedName("Fecha")
        var fecha: String,
        @SerializedName("F300")
        var f300: String? = null,
        @SerializedName("Hora")
        var hora: String? = null,
        @SerializedName("ValF300")
        val valf300: String? = null,
        @SerializedName("UsuarioValida")
        val usuarioValida: String? = null,
        @SerializedName("FechaValida")
        val fechaValida: String? = null
): Parcelable
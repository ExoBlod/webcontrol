package com.webcontrol.android.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CuestionarioResponse(
    @SerializedName("Codigo")
    var codigo: Int? = null,
    @SerializedName("Codctrl_Inicial")
    var codControlInicial: Int? = null,
    @SerializedName("Rut")
    var rut : String? = null,
    @SerializedName("CodCuarentena")
    var codCuarentena: Int? = null,
    @SerializedName("CodFormato")
    var codFormato: String? = null,
    @SerializedName("CodCuestionario")
    var codCuestionario: Int? = null,
    @SerializedName("CodAlternativa")
    var codAlternativa: Int? = null,
    @SerializedName("Dato")
    var dato: String? = null,
    @SerializedName("Comentario")
    var comentario: String? = null,
    @SerializedName("Fecha")
    var fecha: String? = null,
    @SerializedName("FechaSube")
    var fechaSube: String? = null,
    @SerializedName("HoraSube")
    var horaSube: String? = null,
    @SerializedName("FechaValida")
    var fechaValida: String? = null,
    @SerializedName("HoraValida")
    var horaValida: String? = null,
    @SerializedName("UsuarioSube")
    var usuarioSube: String? = null,
    @SerializedName("UsuarioValida")
    var usuarioValida: String? = null
): Parcelable
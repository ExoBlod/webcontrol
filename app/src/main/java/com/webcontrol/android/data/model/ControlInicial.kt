package com.webcontrol.android.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.text.DecimalFormat

@Parcelize
data class ControlInicial (
    @SerializedName("Codigo")
    val id: Int? = null,
    @SerializedName("Rut")
    var rut: String? = null,
    @SerializedName("Division")
    var division: String? = null,
    @SerializedName("Empresa")
    var empresa: String? = null,
    @SerializedName("Fecha")
    var fecha: String? = null,
    @SerializedName("Hora")
    var hora: String? = null,
    @SerializedName("Consentimiento")
    var consentimiento: String? = null,
    @SerializedName("Inicial")
    var inicial: String? = null,
    @SerializedName("F00")
    var formato00: String? = null,
    @SerializedName("F100")
    var formato100: String? = null,
    @SerializedName("ValF00")
    var valf00: String? = null,
    @SerializedName("ValF100")
    var valf100: String? = null,
    @SerializedName("ValInicial")
    var valInicial: String? = null,
    @SerializedName("Positivo")
    var positivo: String? = null,
    @SerializedName("Temperatura")
    var temperatura: Double? = null,
    @SerializedName("Ctrl_saturacion")
    var controlSaturacion: Double? = null,
    @SerializedName("CodCtrlInicialPadre")
    var codControlInicialPadre: Int? = null,
    @SerializedName("FechaVisible")
    var fechaVisible: String? = null
): Parcelable
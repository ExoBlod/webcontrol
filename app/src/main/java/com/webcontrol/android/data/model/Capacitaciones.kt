package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Capacitaciones(
    @SerializedName("IDCHARLA", alternate = ["idcharla"])
    var idCharla: String? = "",
    @SerializedName("RUT", alternate = ["rut"])
    var rut: String? = "",
    @SerializedName("NOMBRES", alternate = ["nombres"])
    var nombres: String? = "",
    @SerializedName("APELLIDOS", alternate = ["apellidos"])
    var apellidos: String? = "",
    @SerializedName("EMPRESA", alternate = ["empresa"])
    var empresa: String? = "",
    @SerializedName("CHARLA", alternate = ["charla"])
    var charla: String? = "",
    @SerializedName("FECHA", alternate = ["fecha"])
    var fecha: String? = "",
    @SerializedName("VENCIMIENTO", alternate = ["vencimiento"])
    var vencimiento: String? = "",
    @SerializedName("HORA", alternate = ["hora"])
    var hora: String? = "",
    @SerializedName("DIVISION", alternate = ["division"])
    var division: String? = "",
    @SerializedName("DIVCOD", alternate = ["divCodigo"])
    var divCodigo: String? = "",
    @SerializedName("ASISTIO", alternate = ["asistio"])
    var asistio: String? = "",
    @SerializedName("APROBO", alternate = ["aprobo"])
    var aprobo: String? = "",
    @SerializedName("NOTA", alternate = ["nota"])
    var nota: String? = "",
    @SerializedName("OBS", alternate = ["obs"])
    var obs: String? = ""
): Serializable
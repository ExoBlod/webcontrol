package com.webcontrol.angloamerican.data.network.response

import com.google.gson.annotations.SerializedName
import com.webcontrol.angloamerican.data.*

class CredentialListCourses (
    @SerializedName(IDRESULTADO)
    var IDResultado :Int = 0,
    @SerializedName(IDRESERVA)
    var IDReserva :Int = 0,
    @SerializedName(CURSO)
    var Curso : Int = 0,
    @SerializedName(CHARLA)
    var Charla : String = "",
    @SerializedName(FECHAEXAMEN)
    var FechaExamen : String = "",
    @SerializedName(VENCIMIENTO)
    var Vencimiento : Int = 0,
    @SerializedName(FECHAVENCIMIENTO)
    var FechaVencimiento : String = "",
)
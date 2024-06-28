package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class ResultadosExamen(
        @SerializedName("IDEXAMENRESERVA")
        var idExamenReserva: Int = 0,

        @SerializedName("IDEXAMEN")
        var idExamen: Int = 0,

        @SerializedName("IDRESERVA")
        var idReserva: Int = 0,

        @SerializedName("RUT")
        var rut: String? = "",

        @SerializedName("NUMPREGUNTAS")
        var numPreguntas: Int = 0,

        @SerializedName("NUMCORRECTAS")
        var numCorrectas: Int = 0,

        @SerializedName("IMEI")
        var imei: String? = "",

        @SerializedName("TIPO")
        var tipo: String? = "",

        @SerializedName("RESPUESTA")
        var respuesta: Int = 0,

        @SerializedName("LISTRESPUESTAS")
        private var ListRespuestas: List<RespuestasHist>? = null
) {
    fun setListRespuestas(listRespuestas: List<RespuestasHist>?) {
        ListRespuestas = listRespuestas
    }
}
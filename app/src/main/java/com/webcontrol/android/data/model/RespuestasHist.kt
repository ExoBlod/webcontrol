package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class RespuestasHist(
    @JvmField
    @SerializedName("IDEXAMENRESERVA")
    var idExamenReserva: Int = 0,

    @JvmField
    @SerializedName("IDPREGUNTA")
    var idPregunta: Int? = null,

    @SerializedName("IDEXAMEN")
    var idExamen: Int? = null,

    @SerializedName("IDRESERVA")
    var idReserva: Int? = null,

    @SerializedName("RUT")
    var rut: String? = null,

    @JvmField
    @SerializedName("IDRESPUESTA")
    var idRespuesta: Int? = null,

    @SerializedName("PREGRESPONDIDA")
    var respondida: Int = 0,

    @SerializedName("OBSERVACION")
    private var observacion: String? = null
) {
    fun setObservacion(observacion: String?) {
        this.observacion = observacion
    }
}
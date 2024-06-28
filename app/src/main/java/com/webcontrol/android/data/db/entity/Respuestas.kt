package com.webcontrol.android.data.db.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "respuestas",
    primaryKeys = ["idSistema", "idExamenReserva", "idPregunta", "idExamen", "idReserva", "rut", "idRespuesta"]
)
class Respuestas {
    @NonNull
    var idSistema: String = ""

    @NonNull
    @SerializedName("IDEXAMEN_CURSORESERVA")
    var idExamenReserva: Int = 0

    @NonNull
    @SerializedName("IDPREGUNTA")
    var idPregunta: Int = 0

    @NonNull
    @SerializedName("IDEXAMEN")
    var idExamen: Int = 0

    @NonNull
    @SerializedName("IDRESERVA")
    var idReserva: Int = 0

    @NonNull
    @SerializedName("RUT")
    var rut: String = ""

    @NonNull
    @SerializedName("IDRESPUESTA")
    var idRespuesta: Int = 0

    @SerializedName("RESPUESTA")
    var respuesta: String? = ""

    @SerializedName("ORDEN")
    var orden: Int? = 0

    @SerializedName("ESCORRECTA")
    var escorrecta = false

    var selecciono = false

    constructor() {}

    @Ignore
    constructor(
        @NonNull idSistema: String,
        @NonNull idPregunta: Int,
        @NonNull idExamen: Int,
        @NonNull idReserva: Int,
        @NonNull rut: String,
        @NonNull idRespuesta: Int,
        respuesta: String?,
        orden: Int?,
        escorrecta: Boolean
    ) {
        this.idPregunta = idPregunta
        this.idExamen = idExamen
        this.idReserva = idReserva
        this.rut = rut
        this.idRespuesta = idRespuesta
        this.respuesta = respuesta
        this.orden = orden
        this.idSistema = idSistema
        this.escorrecta = escorrecta
    }
}
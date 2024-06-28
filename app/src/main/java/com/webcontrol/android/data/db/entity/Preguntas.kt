package com.webcontrol.android.data.db.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "preguntas",
    primaryKeys = ["idSistema", "idExamenReserva", "idPregunta", "idExamen", "idReserva", "rut"]
)
class Preguntas {
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

    @SerializedName("PREGUNTA")
    var pregunta: String? = ""

    @SerializedName("ORDEN")
    var orden: Int? = 0

    @SerializedName("SECOMENTA")
    @ColumnInfo(name = "secomenta")
    var isSecomenta: Boolean = false

    @SerializedName("RESPORDENADAS")
    var respOrdenadas: Boolean = false

    var comentario: String? = ""

    var respondida: Int = 0

    @Ignore
    @SerializedName("ListaRespuestas")
    var listRespuestas: List<Respuestas> = ArrayList()

    constructor() {}

    @Ignore
    constructor(
        @NonNull idSistema: String,
        @NonNull idPregunta: Int,
        @NonNull idExamen: Int,
        @NonNull idReserva: Int,
        @NonNull rut: String,
        pregunta: String?,
        orden: Int?,
        secomenta: Boolean,
        respOrdenadas: Boolean,
        ListRespuestas: List<Respuestas>
    ) {
        this.idPregunta = idPregunta
        this.idExamen = idExamen
        this.idReserva = idReserva
        this.rut = rut
        this.pregunta = pregunta
        this.orden = orden
        this.isSecomenta = secomenta
        this.respOrdenadas = respOrdenadas
        this.listRespuestas = ListRespuestas
        this.idSistema = idSistema
    }
}
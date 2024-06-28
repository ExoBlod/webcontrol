package com.webcontrol.android.data.db.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "examenes",
    primaryKeys = ["idSistema", "idExamenReserva", "idCurso", "idReserva", "rut", "idExamen"]
)
class Examenes {
    var idSistema: String = ""

    @NonNull
    @SerializedName("IDEXAMEN_CURSORESERVA")
    var idExamenReserva: Int = 0

    @NonNull
    @SerializedName("IDCURSO")
    var idCurso: Int = 0

    @SerializedName("IDRESERVA")
    var idReserva: Int = 0

    @SerializedName("RUT")
    var rut: String = ""

    @SerializedName("IDEXAMEN")
    var idExamen: Int = 0

    @SerializedName("IDENCUESTA")
    var idEncuesta: Int = 0

    @SerializedName("NOMEXAMEN")
    var nomExamen: String? = ""

    @SerializedName("NOMENCUESTA")
    var nomEncuesta: String? = ""

    @SerializedName("DESCEXAMEN")
    var descExamen: String? = ""

    @SerializedName("FECHA_PROGRAMADA_EXAMEN")
    var fecha_programada: String? = ""

    @SerializedName("HORA_PROGRAMADA_EXAMEN")
    var hora_programada: String? = ""

    @SerializedName("TIEMPO_TOTAL_EXAMEN")
    var tiempoTotal: String? = ""

    @SerializedName("APROBO")
    var aprobo: String? = ""

    @SerializedName("ESTADO_EXAMEN")
    var estado: Int = 0

    @NonNull
    @SerializedName("FINAL")
    var examenFinal: Int = 0

    @NonNull
    @SerializedName("ORDEN")
    var orden: Int = 0

    @SerializedName("TIPO")
    var tipo: String? = ""

    var iniciado: Boolean = false

    var fechaTermino: String? = ""

    var horaTermino: String? = ""

    var dia: Int = 0

    var fechaExamen: String? = ""

    constructor() {}

    @Ignore
    constructor(
        @NonNull idSistema: String,
        @NonNull idExamenReserva: Int,
        @NonNull idCurso: Int,
        @NonNull idReserva: Int,
        @NonNull rut: String,
        @NonNull idExamen: Int,
        idEncuesta: Int,
        nomExamen: String?,
        nomEncuesta: String?,
        descExamen: String?,
        fecha_programada: String?,
        hora_programada: String?,
        tiempoTotal: String?,
        aprobo: String?,
        estado: Int,
        iniciado: Boolean,
        fechaTermino: String?,
        horaTermino: String?,
        dia: Int,
        fechaExamen: String?
    ) {
        this.idExamenReserva = idExamenReserva
        this.idCurso = idCurso
        this.idReserva = idReserva
        this.rut = rut
        this.idExamen = idExamen
        this.idEncuesta = idEncuesta
        this.nomExamen = nomExamen
        this.nomEncuesta = nomEncuesta
        this.descExamen = descExamen
        this.fecha_programada = fecha_programada
        this.hora_programada = hora_programada
        this.tiempoTotal = tiempoTotal
        this.aprobo = aprobo
        this.estado = estado
        this.iniciado = iniciado
        this.fechaTermino = fechaTermino
        this.horaTermino = horaTermino
        this.idSistema = idSistema
        this.dia = dia
        this.fechaExamen = fechaExamen
    }
}
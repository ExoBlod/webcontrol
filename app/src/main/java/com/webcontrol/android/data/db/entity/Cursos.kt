package com.webcontrol.android.data.db.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName

@Entity(tableName = "cursos", primaryKeys = ["idSistema", "idCurso", "idReserva", "rut"])
class Cursos {

    @NonNull
    @SerializedName("IDCURSO")
    var idCurso :Int = 0

    @NonNull
    @SerializedName("IDRESERVA")
    var idReserva :Int  = 0

    @NonNull
    @SerializedName("RUT")
    var rut: String = ""

    @SerializedName("NOMCURSO")
    var nomCurso: String? = ""

    @SerializedName("ORADOR")
    var orador: String? = ""

    @SerializedName("MIN_APROBACION")
    var minAprobacion: String? = ""

    @SerializedName("FECHAHORACURSO")
    var fechaHoraCurso: String? = ""

    @NonNull
    var idSistema: String = ""

    @Ignore
    @SerializedName("ListaExamenes")
    var listExamenes: List<Examenes> = ArrayList()

    var dia :Int = 0
    var fechaExamen: String? = ""

    constructor() {}

    @Ignore
    constructor(
        @NonNull idSistema: String,
        @NonNull idCurso: Int,
        @NonNull idReserva: Int,
        @NonNull rut: String,
        nomCurso: String?,
        orador: String?,
        minAprobacion: String?,
        fechaHoraCurso: String?,
        listExamenes: List<Examenes>,
        dia: Int,
        fechaExamen: String?
    ) {
        this.idCurso = idCurso
        this.idReserva = idReserva
        this.rut = rut
        this.nomCurso = nomCurso
        this.orador = orador
        this.minAprobacion = minAprobacion
        this.fechaHoraCurso = fechaHoraCurso
        this.listExamenes = listExamenes
        this.idSistema = idSistema
        this.dia = dia
        this.fechaExamen = fechaExamen
    }
}
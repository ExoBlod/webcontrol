package com.webcontrol.android.data.db.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "message")
class Message {
    @PrimaryKey
    @NonNull
    @SerializedName("ID")
    var id :Int= 0

    @SerializedName("RUT")
    var rut: String? = ""

    @SerializedName("MENSAJE")
    var mensaje: String? = ""

    @SerializedName("FECHA")
    var fecha: String? =""

    @SerializedName("HORA")
    var hora: String? = ""

    @SerializedName("ESTADO")
    var estado :Int = 0

    @SerializedName("FECHAMODIFICACION")
    var fechaModificacion: String? = ""

    var color :Int = -14043402

    @SerializedName("REMITENTE")
    var remitente: String? = ""

    @SerializedName("ASUNTO")
    var asunto: String? = ""

    @SerializedName("IMPORTANTE")
    var isImportant :Boolean = false

    var importanteSincronizado :Boolean  = false

    var estadoSincronizado :Boolean = false

    @SerializedName("IDSYNC")
    var idSync: Long = 0

    @SerializedName("MANDANTE")
    var MANDANTE: String? = ""

    @SerializedName("NOMEMPRESA")
    var NOMEMPRESA: String? = ""

    constructor() {
        estadoSincronizado = true
        importanteSincronizado = true
    }

    @Ignore
    constructor(
        id: Int,
        rut: String?,
        mensaje: String?,
        fecha: String?,
        hora: String?,
        estado: Int
    ) {
        this.id = id
        this.rut = rut
        this.mensaje = mensaje
        this.fecha = fecha
        this.hora = hora
        this.estado = estado
    }

    val isEliminado: Boolean
        get() = estado == 3
    val isLeido: Boolean
        get() = estado == 2
}
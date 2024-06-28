package com.webcontrol.angloamerican.data.db.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "preacceso_mina")
class PreaccesoMina {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id :Int= 0
    @NonNull
    var fecha: String = ""
    @NonNull
    var hora: String = ""
    @NonNull
    var conductor: String = ""
    @NonNull
    var patente: String = ""
    @NonNull
    var sentido: String = ""
    @NonNull
    var division: String = ""
    var divisionNombre: String? = ""
    @NonNull
    var local: String = ""
    var localNombre: String? = ""
    @NonNull
    var estado: String = ""
    var createdAt: String? = ""
    var updatedAt: String? = ""
    @NonNull
    var viaje: String = ""
    @NonNull
    var passengers: String = ""

    constructor() {}

    @Ignore
    constructor(
        id: Int,
        @NonNull fecha: String,
        @NonNull hora: String,
        @NonNull conductor: String,
        @NonNull patente: String,
        @NonNull sentido: String,
        @NonNull division: String,
        divisionNombre: String?,
        @NonNull local: String,
        localNombre: String?,
        @NonNull estado: String,
        createdAt: String?,
        updatedAt: String?,
        @NonNull viaje: String,
        @NonNull passengers: String
    ) {
        this.id = id
        this.fecha = fecha
        this.hora = hora
        this.conductor = conductor
        this.patente = patente
        this.sentido = sentido
        this.division = division
        this.divisionNombre = divisionNombre
        this.local = local
        this.localNombre = localNombre
        this.estado = estado
        this.createdAt = createdAt
        this.updatedAt = updatedAt
        this.viaje = viaje
        this.passengers=passengers
    }
}
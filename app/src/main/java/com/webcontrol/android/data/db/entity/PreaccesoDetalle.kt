package com.webcontrol.android.data.db.entity

import androidx.annotation.NonNull
import androidx.room.*

@Entity(
    tableName = "preacceso_detalle",
    foreignKeys = [ForeignKey(
        entity = Preacceso::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("preacceso_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
class PreaccesoDetalle {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    var id = 0

    @ColumnInfo(name = "preacceso_id")
    @NonNull
    var preaccesoId = 0

    @NonNull
    var rut: String = ""

    @NonNull
    var hora: String = ""

    @NonNull
    var companiaId: String = ""
    var companiaNombre: String? = ""
    var error: String? = ""
    var centroCosto: String? = ""
    var ost: String? = ""
    var tipoPase: String? = ""

    @NonNull
    var estado: String = ""
    var createdAt: String? =""
    var updatedAt: String? = ""

    @NonNull
    var nombreWorker: String =""
    var apellidoWorker: String? = ""
    @ColumnInfo(name = "autor")
    var isAutor = false
    var vehiculo: String? = ""

    var detalleAutor: String? = ""
    @ColumnInfo(name = "validated")
    var isValidated = false

    constructor() {}

    @Ignore
    constructor(
        preaccesoId: Int,
        @NonNull rut: String,
        @NonNull hora: String,
        @NonNull companiaId: String,
        companiaNombre: String?,
        error: String?,
        centroCosto: String?,
        ost: String?,
        tipoPase: String?,
        @NonNull estado: String,
        createdAt: String?,
        updatedAt: String?,
        @NonNull nombreWorker: String,
        apellidoWorker: String?,
        autor: Boolean,
        vehiculo: String?,
        detalleAutor: String?,
        validated: Boolean
    ) {
        this.preaccesoId = preaccesoId
        this.rut = rut
        this.hora = hora
        this.companiaId = companiaId
        this.companiaNombre = companiaNombre
        this.error = error
        this.centroCosto = centroCosto
        this.ost = ost
        this.tipoPase = tipoPase
        this.estado = estado
        this.createdAt = createdAt
        this.updatedAt = updatedAt
        this.nombreWorker = nombreWorker
        this.apellidoWorker = apellidoWorker
        this.isAutor = autor
        this.vehiculo = vehiculo
        this.detalleAutor = detalleAutor
        this.isValidated = validated
    }
}
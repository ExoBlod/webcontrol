package com.webcontrol.android.data.db.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "inspeccion_vehicular")
class InspeccionVehicular (

    @NonNull
    @ColumnInfo(name="operador")
    var operador:String="",

    @NonNull
    @ColumnInfo(name="supervisor")
    var supervisor: String="",

    @PrimaryKey
    @NonNull
    @ColumnInfo(name="placa")
    var placa: String="",

    @NonNull
    @ColumnInfo(name="marca_modelo")
    var marca: String="",

    @NonNull
    @ColumnInfo(name="turno")
    var turno: String="",

    @NonNull
    @ColumnInfo(name="odometria")
    var odometria: Int=0

    )
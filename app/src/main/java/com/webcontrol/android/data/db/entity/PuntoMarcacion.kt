package com.webcontrol.android.data.db.entity

import com.google.gson.annotations.SerializedName
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "puntos_marcacion")
data class PuntoMarcacion(
    @SerializedName("Id")
    @PrimaryKey
    val codigo: Int,

    @SerializedName("Name")
    var nombre: String,

    @SerializedName("Description")
    val descripcion: String,

    @SerializedName("DivisionId")
    val division: String,

    @SerializedName("LocalId")
    val local: String,

    @SerializedName("Latitude")
    val latitud: Double,

    @SerializedName("Longitude")
    val longitud: Double,

    @SerializedName("Radius")
    var radio: Int,

    @SerializedName("InOut")
    @ColumnInfo(name = "in_out")
    var inOut: String,

    @SerializedName("Principal")
    var mandante: String

)

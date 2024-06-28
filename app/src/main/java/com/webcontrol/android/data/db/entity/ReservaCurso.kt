package com.webcontrol.android.data.db.entity


import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "Reserva_Curso", primaryKeys = ["code_reserve","code_course"])
class ReservaCurso (

    @NonNull
    @SerializedName("CODIGO")
    @ColumnInfo(name="code_reserve")
    var codeReserve: Int,

    @NonNull
    @SerializedName("CURSO")
    @ColumnInfo(name="code_course")
    var codeCourse: Int,

    @SerializedName("CHARLA")
    @ColumnInfo(name="name_course")
    var nameCourse: String?,

    @SerializedName("FECHA")
    @ColumnInfo(name="date_course")
    var dateCourse: String? = "",

    @SerializedName("HORA")
    @ColumnInfo(name="time_course")
    var timeCourse: String? = "",

    @SerializedName("FECHARES")
    @ColumnInfo(name="date_reserve")
    var dateReserve: String? = "",

    @SerializedName("HORARES")
    @ColumnInfo(name="time_reserve")
    var timeReserve: String? = "",

    @SerializedName("HORAS")
    @ColumnInfo(name="duration")
    var duration: Double? = null,

    @SerializedName("STATUS")
    @ColumnInfo(name="status_reserve")
    var statusReserve: String? = "",

    @SerializedName("LUGAR")
    @ColumnInfo(name="place")
    var place: String? = "",

    @SerializedName("OBLIGATORIO")
    @ColumnInfo(name="required")
    var required: String? = "",

    @SerializedName("SALA")
    @ColumnInfo(name="sala")
    var sala: String? = "",

    @SerializedName("CUPOS")
    @ColumnInfo(name="capacity_free")
    var capacity: Int? = null,

    @SerializedName("UBICACION")
    @ColumnInfo(name="location")
    var location: String? = null,

    @SerializedName("DIVISIONES")
    @ColumnInfo(name="divisions")
    var divisions: String? = null,

    @SerializedName("ORADOR")
    @ColumnInfo(name="orador")
    var orador: String? = null,

) : Serializable {}
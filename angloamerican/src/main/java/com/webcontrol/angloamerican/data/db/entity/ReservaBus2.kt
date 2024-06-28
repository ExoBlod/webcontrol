package com.webcontrol.angloamerican.data.db.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import com.webcontrol.angloamerican.utils.Utils
import com.webcontrol.core.common.TypeFactory
import com.webcontrol.core.common.Visitable
import java.io.Serializable

@Entity(tableName = "reserva_bus2", primaryKeys = ["code_prog", "worker_id"])
class ReservaBus2(

    @NonNull
    @SerializedName("ProgId")
    @ColumnInfo(name = "code_prog")
    var codeProg: Long,

    @NonNull
    @SerializedName("WorkerId")
    @ColumnInfo(name = "worker_id")
    var workerId: String,

    @SerializedName("TransactionId")
    @ColumnInfo(name = "transac_id")
    var transacId: String? = "",

    @SerializedName("VehicleId")
    @ColumnInfo(name = "name_bus")
    val codeBus: String,

    @SerializedName("OrigenId")
    @ColumnInfo(name = "source_bus")
    val source: String,

    @SerializedName("DestinoId")
    @ColumnInfo(name = "destiny_bus")
    val destiny: String,

    @SerializedName("Duration")
    @ColumnInfo(name = "duration")
    val duration: Int,

    @SerializedName("Fecha")
    @ColumnInfo(name = "date")
    val date: String,

    @SerializedName("Hora")
    @ColumnInfo(name = "time")
    val time: String,

    @SerializedName("FechaReserva")
    @ColumnInfo(name = "date_reserve")
    val dateReserve: String,

    @SerializedName("HoraReserva")
    @ColumnInfo(name = "time_reserve")
    val timeReserve: String,

    @ColumnInfo(name = "status")
    val status: String? = "P",

    @SerializedName("EstadoReserva")
    @ColumnInfo(name = "status_reserve")
    var statusReserve: String? = "",


    @SerializedName("Asiento")
    @ColumnInfo(name = "asiento")
    var seat: Int?,

    @SerializedName("SyncId")
    @ColumnInfo(name = "sync_id")
    var syncId: Int?,

    @SerializedName("Conductor1")
    @ColumnInfo(name = "driver_1")
    var driver1: String? = "",


    @SerializedName("Conductor2")
    @ColumnInfo(name = "driver_2")
    var driver2: String? = "",


    @SerializedName("FechaCancela")
    @ColumnInfo(name = "fecha_cancelada")
    val fechaCancelada:String?=null,

    @SerializedName("HoraCancela")
    @ColumnInfo(name = "hora_cancelada")
    val horaCancelada:String?=null


) : Serializable, Visitable<ReservaBus2> {
    override fun type(typeFactory: TypeFactory<ReservaBus2>): Int {
        return typeFactory.type(this)
    }

    override fun toString(): String {
        return "ReservaBus2(codeProg=$codeProg, transacId=$transacId, statusReserve=$statusReserve)"
    }

    @JvmName("fechaFormat")
    fun formatDate(): String {
        return Utils.getNiceDate(date)
    }

}
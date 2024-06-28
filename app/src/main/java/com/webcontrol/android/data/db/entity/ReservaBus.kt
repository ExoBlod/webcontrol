package com.webcontrol.android.data.db.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "Reserva_Bus", primaryKeys = ["prog_id","worker_id"])
class ReservaBus (
    @NonNull
    @SerializedName("ProgId")
    @ColumnInfo(name="prog_id")
    var progId: Int,

    @NonNull
    @SerializedName("WorkerId")
    @ColumnInfo(name="worker_id")
    var workerId: String,

    @SerializedName("VehicleId")
    @ColumnInfo(name="vehicle_id")
    val vehicleId: String? = "",

    @SerializedName("OrigenId")
    @ColumnInfo(name="origen_id")
    val origenId: String? = "",

    @SerializedName("OrigenName")
    @ColumnInfo(name="origen_name")
    var origenName: String? = "",

    @SerializedName("DestinoId")
    @ColumnInfo(name="destino_id")
    val destinoId: String? = "",

    @SerializedName("DestinoName")
    @ColumnInfo(name="destino_name")
    var destinoName: String? = "",

    @SerializedName("Fecha")
    var fecha: String? = "",

    @SerializedName("Hora")
    var hora: String? = "",

    @SerializedName("TransactionId")
    @ColumnInfo(name="transaction_id")
    var transactionId: String? = "",

    @SerializedName("Asiento")
    var asiento: Int? = 0,

    @SerializedName("UtilizoReserva")
    var utilizo: String? = "NO",

    @SerializedName("EstadoReserva")
    var estado: String? = "NO",

    @SerializedName("SyncId")
    @ColumnInfo(name = "sync_id")
    val syncId: Int
) : Serializable {}
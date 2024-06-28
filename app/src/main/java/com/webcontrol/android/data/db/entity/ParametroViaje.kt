package com.webcontrol.android.data.db.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "parametro_viaje")
class ParametroViaje (
    @NonNull
    @SerializedName("TIEMPO_CANCELA")
    @ColumnInfo(name="tiempo_cancela")
    var tiempoCancela: Int,

    @NonNull
    @SerializedName("TIEMPO_RESERVA")
    @ColumnInfo(name="tiempo_reserva")
    var tiempoReserva: Int,

    @SerializedName("CONTROL_PEREZ")
    @ColumnInfo(name="control_perez")
    var controlPerez: String?,

    @SerializedName("IDA_VUELTA")
    @ColumnInfo(name="ida_vuelta")
    var idaVuelta: String? = "",

    @SerializedName("DIAS_CASTIGO")
    @ColumnInfo(name="dias_castigo")
    var diasCastigo: String? = "",

    @SerializedName("PORC_NOUTILIZA")
    @ColumnInfo(name="porc_noutiliza")
    var porcNoutiliza: String? = "",

    @SerializedName("PERIODO_NOUTILIZA")
    @ColumnInfo(name="periodo_noutiliza")
    var periodoNoutiliza: String? = "",

    @SerializedName("CONTROL_NOUTILIZA")
    @ColumnInfo(name="control_noutiliza")
    var controlNoutiliza: Double? = null,

    @SerializedName("GLOSA_TICKET")
    @ColumnInfo(name="glosa_ticket")
    var glosaTicket: String? = "",

    @SerializedName("GLOSA")
    @ColumnInfo(name="glosa")
    var glosa: String? = "",


    @SerializedName("CORREO_ADICIONAL")
    @ColumnInfo(name="correo_adicional")
    var correoAdicional: String? = "",

    @SerializedName("CONTROL_MANTENCION")
    @ColumnInfo(name="control_mantencion")
    var controlMantencion: String? = "",

    @SerializedName("CANCELA_REALIZADO")
    @ColumnInfo(name="cancela_realizado")
    var cancelaRealizado: Int? = null,

    @SerializedName("CANTIDAD_SUBIDAS")
    @ColumnInfo(name="cantidad_subidas")
    var cantidadSubidas: String? = null,

    @SerializedName("CANTIDAD_BAJADAS")
    @ColumnInfo(name="cantidad_bajadas")
    var cantidadBajadas: String? = null,


    ) : Serializable {}
package com.webcontrol.android.data.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import com.google.gson.annotations.SerializedName

data class ParametroViaje(
    @NonNull
    @SerializedName("TIEMPO_CANCELA")
    var tiempoCancela: Int,

    @NonNull
    @SerializedName("TIEMPO_RESERVA")
    var tiempoReserva: Int,

    @SerializedName("CONTROL_PEREZ")
    var controlPerez: String?,

    @SerializedName("IDA_VUELTA")
    var idaVuelta: String? = "",

    @SerializedName("DIAS_CASTIGO")
    var diasCastigo: Int,

    @SerializedName("PORC_NOUTILIZA")
    var porcNoutiliza: Int,

    @SerializedName("PERIODO_NOUTILIZA")
    var periodoNoutiliza: Int,

    @SerializedName("CONTROL_NOUTILIZA")
    var controlNoutiliza: String? = "",

    @SerializedName("GLOSA_TICKET")
    var glosaTicket: String? = "",

    @SerializedName("GLOSA")
    var glosa: String? = "",


    @SerializedName("CORREO_ADICIONAL")
    var correoAdicional: String? = "",

    @SerializedName("CONTROL_MANTENCION")
    @ColumnInfo(name="control_mantencion")
    var controlMantencion: String? = "",

    @SerializedName("CANCELA_REALIZADO")
    var cancelaRealizado: String? = "",

    @SerializedName("CANTIDAD_SUBIDAS")
    var cantidadSubidas: Int,

    @SerializedName("CANTIDAD_BAJADAS")
    var cantidadBajadas: Int,

    ){
}

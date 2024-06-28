package com.webcontrol.angloamerican.data


import com.google.gson.annotations.SerializedName
import com.webcontrol.core.common.TypeFactory
import com.webcontrol.core.common.Visitable

data class ResponseReservaBus(

    @SerializedName("IdProg")
    var codeProg: Long? = null,

    @SerializedName("Patente")
    val patente: String? = null,
    @SerializedName("Origen")
    val source: String? = null,
    @SerializedName("Destino")
    val destiny: String? = null,
    @SerializedName("Duracion")
    val duration: String? = null,
    @SerializedName("Fecha")
    val date: String? = null,
    @SerializedName("Hora")
    val time: String? = null,
    @SerializedName("Capacidad")
    val spaceAvailable: Int? = null,

    @SerializedName("Status")
    val status: String? = null,

    @SerializedName("Message")
    val message: String? = null,

    @SerializedName("Disponible")
    val disponible: Int? = null,

    var colorBus: Int = 0

) : Visitable<ResponseReservaBus> {
    override fun type(typeFactory: TypeFactory<ResponseReservaBus>): Int {
        return typeFactory.type(this)
    }

    fun setColorBus() {
        colorBus = (1..9).random()
    }

    @JvmName("strDuration")
    fun getDuration(): String {
        return "${this.duration}Hrs"
    }

    override fun toString(): String {
        return "ResponseReservaBus(name='$patente', source='$source', status='$status', message='$message')"
    }
}
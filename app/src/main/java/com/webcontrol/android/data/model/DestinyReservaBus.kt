package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class DestinyReservaBus(
    @SerializedName("Destino")
    var id: String,

    @SerializedName("DirDestino")
    var nombre: String

) {
    override fun toString(): String {
        return id
    }
}
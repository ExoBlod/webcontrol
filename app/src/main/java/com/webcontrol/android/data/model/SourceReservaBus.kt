package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName


data class SourceReservaBus(
    @SerializedName("Origen")
    var id: String,

    @SerializedName("DirOrigen")
    var nombre: String

) {
    override fun toString(): String {
        return id
    }
}
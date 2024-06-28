package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class Division(
        @SerializedName("DivisionId", alternate = ["divisionId"])
        var id: String,

        @SerializedName("Name", alternate = ["name"])
        var nombre: String,

        @SerializedName("Country", alternate = ["country"])
        var pais: String = ""
) {
    override fun toString(): String {
        return nombre
    }
}
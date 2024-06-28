package com.webcontrol.angloamerican.data.model

import com.google.gson.annotations.SerializedName

data class Division(
        @SerializedName("DivisionId")
        var id: String,

        @SerializedName("Name")
        var nombre: String,

        @SerializedName("Country")
        var pais: String = ""
) {
    override fun toString(): String {
        return nombre
    }
}
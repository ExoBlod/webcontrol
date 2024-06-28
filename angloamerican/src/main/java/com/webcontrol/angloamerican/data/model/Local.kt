package com.webcontrol.angloamerican.data.model

import com.google.gson.annotations.SerializedName

data class Local(
        @SerializedName("LocalId")
        var id: String,

        @SerializedName("Description")
        var nombre: String
) {
    override fun toString(): String {
        return nombre
    }
}
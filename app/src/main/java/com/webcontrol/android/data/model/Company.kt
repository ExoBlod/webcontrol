package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class Company(
        @SerializedName("Acronym")
        private val acronimo: String,

        @SerializedName("Name")
        var nombre: String
) {
    override fun toString(): String {
        return nombre
    }
}
package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class Pais(
        @SerializedName("PAIS")
        var pais: String? = "SIN PAIS"
){
    override fun toString(): String {
        return pais!!
    }
}

data class RegionPais(
        @SerializedName("REGION")
        val region: String? = null,
        @SerializedName("DESCRIPCION")
        val descripcion: String? = ""
){
    override fun toString(): String {
        return descripcion!!
    }
}

data class CiudadRegion(
        @SerializedName("CIUDAD")
        val ciudad: String? = null
){
    override fun toString(): String {
        return ciudad!!
    }
}

data class ComunaCiudad(
        @SerializedName("COMUNA")
        val distrito: String? = null
){
    override fun toString(): String {
        return distrito!!
    }
}
package com.webcontrol.android.ui.covid.antapaccay.initialdata

data class Pais(
        val nombre: String?,
        val continente: String?
){
    override fun toString(): String {
        return nombre ?: ""
    }
}

data class Region(
        val nombre: String?,
        val descripcion: String?,
        val pais: String?
){
    override fun toString(): String {
        return nombre ?: ""
    }
}

data class Ciudad(
        val nombre: String?,
        val region: String?
){
    override fun toString(): String {
        return nombre ?: ""
    }
}

data class Comuna(
        val nombre: String?,
        val ciudad: String?
){
    override fun toString(): String {
        return nombre ?: ""
    }
}
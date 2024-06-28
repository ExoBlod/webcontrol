package com.webcontrol.android.data.network.dto

data class OwnDocs(
    val LISTA_FECHAS: List<DateOwnDocs>,
    val LISTA_RECHAZOS: List<RejectOwnDocs>,
    val ARCHIVO: String,
    val ARCHIVO2: String,
    val CERTFECHA: String,
    val CERTHORA: String,
    val CERTUSUARIO: String,
    val FECHASUBE: String,
    val FECHA_MOD: String,
    val HORASUBE: String,
    val HORA_MOD: String,
    val ID: String,
    val ID_DOC: Int,
    val NOMBRE: String,
    val NOMBRE_ARCHIVO: String,
    val RUT: String,
    val TIPOCONTENIDO: String,
    val USUARIO: String,
    val VALIDADO: String
)
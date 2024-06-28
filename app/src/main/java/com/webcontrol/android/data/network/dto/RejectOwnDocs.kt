package com.webcontrol.android.data.network.dto

data class RejectOwnDocs(
    val ID: String,
    val ID_DOC: Int,
    val ID_TIPO_RECHAZO: String,
    val RUT: String,
    val TIPO_RECHAZO: String,
    val USUARIO: String
)
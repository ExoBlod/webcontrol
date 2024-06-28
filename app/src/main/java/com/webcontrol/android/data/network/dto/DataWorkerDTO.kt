package com.webcontrol.android.data.network.dto

data class DataWorkerDTO(
    val RUT: String,
    val NOMBRES: String? = "",
    val APELLIDOS: String? = "",
    val TIPO_SANGRE: String? = "",
    val EMPRESA: String? = "",
    val AREA: String? = "",
    val PUESTO: String? = "",
    val AUTORIZADO_CONDUCIR: String? = "",
    val FECHA_EXPEDICION: String? = "",
    val FECHA_VENCIMIENTO: String? = "",
    val NROLICENCIA: String? = "",
    val CLASE_CATEGORIA: String? = "",
    val FECHA_REVALIDACION: String? = "",
    val RESTRICCIONES: String? = "",
)

package com.webcontrol.angloamerican.data.network.response

data class HistoryBookCourseData(
    val IdResultado: Int,
    val IdReserva: Int,
    val Charla: String,
    val Rut: String,
    val NameWorker: String,
    val IdExamen: Int,
    val NumPreguntas: Int,
    val NumCorrectas: Int,
    val DateExam: String,
    val Imei: String,
    val Nota: Int,
    var Aprobo: Any,
    val IdSync: Int,
    var Url: String
)
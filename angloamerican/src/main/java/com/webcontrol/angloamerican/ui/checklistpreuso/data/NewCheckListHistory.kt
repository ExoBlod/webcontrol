package com.webcontrol.angloamerican.ui.checklistpreuso.data


data class NewCheckListHistory(
    val checklistDate: String,
    val checklistInstanceId: Int,
    val checklistName: String,
    val checklistRight: Int = 0,
    val placa: String?="SIN PLACA",
    val validado: Int = 0,
    val vehiculoId: String,
    val workerId: String,
    val workerName: String,
    var status: String?="E1"
)
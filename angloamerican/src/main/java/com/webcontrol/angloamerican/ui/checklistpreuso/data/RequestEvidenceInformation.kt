package com.webcontrol.angloamerican.ui.checklistpreuso.data

data class RequestEvidenceInformation(
    val idCheck: Int,
    val idCheckDet: Int,
    val idTipo: String,
    val obs: String,
    val workerID: String,
    val workerId: String
)
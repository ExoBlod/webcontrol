package com.webcontrol.angloamerican.ui.checklistpreuso.data

data class RequestEvidenceInsert(
    val checkDetail: Int,
    val checkingDetail: Int,
    val valor: String,
    val workerID: String
)
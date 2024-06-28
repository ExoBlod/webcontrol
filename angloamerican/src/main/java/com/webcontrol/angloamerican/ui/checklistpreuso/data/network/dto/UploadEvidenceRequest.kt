package com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto

data class UploadEvidenceRequest(
    val EvidencePhoto: String,
    val IdType: String,
    val IdCheck: Int,
    val IdCheckingHead: Int,
    val IdCheckInDet: Int,
    val IdCheckGroup: Int
)
package com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto

import com.google.gson.annotations.SerializedName

data class WorkerResponse(
    @SerializedName(value = "WorkerId")
    var workerId: String,

    @SerializedName(value = "SupervisorName")
    var supervisorName: String,

    @SerializedName(value = "WorkerName")
    var workerName: String,

    @SerializedName(value = "CompanyId")
    var companyId: String,

    @SerializedName(value = "DivisionId")
    var divisionId: String,

    @SerializedName(value = "IsSignature")
    var isSignature: Integer,

    @SerializedName(value = "InspectionQuery")
    var inspectionQuery: Int,

    @SerializedName(value = "IsDriver")
    var isDriver: String,

    @SerializedName(value = "IsMandante")
    var isMandante: String,
)
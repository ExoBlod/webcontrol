package com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto

import com.google.gson.annotations.SerializedName

class HistoryByPlateResponse(
    @SerializedName("ChecklistName")
    var checklistName: String,

    @SerializedName("workerName")
    var workerName: String,

    @SerializedName("checkingInHead")
    var checkingInHead: Int,

    @SerializedName("plate")
    var plate: String,

    @SerializedName("WorkerId")
    var workerId: String,

    @SerializedName("ChecklistDate")
    var checklistDate: String,

    @SerializedName("validate")
    var validate: Int,

    @SerializedName("ChecklistRight")
    var checklistRight: Int,

    @SerializedName("checklistStatus")
    var checklistStatus: String
)
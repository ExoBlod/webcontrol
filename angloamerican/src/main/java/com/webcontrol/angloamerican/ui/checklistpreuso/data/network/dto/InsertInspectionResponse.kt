package com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto

import com.google.gson.annotations.SerializedName

data class InsertInspectionResponse(
    @SerializedName(value = "CHECKINGHEAD")
    var checkingHead: String = "",
)

package com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto

import com.google.gson.annotations.SerializedName

data class SaveAnswersResponse(
    @SerializedName(value = "Result")
    var result: String,
)

package com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto

import com.google.gson.annotations.SerializedName

data class QuestionListByIdRequest (
    @SerializedName("IdCheckInHead")
    var idCheckInHead: Int
)


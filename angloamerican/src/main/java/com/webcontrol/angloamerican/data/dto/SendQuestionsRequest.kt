package com.webcontrol.angloamerican.data.dto

import com.google.gson.annotations.SerializedName
import com.webcontrol.angloamerican.data.network.response.QuestionData

data class SendQuestionsRequest(
    @SerializedName("Imei")
    val imei: String = "",

    @SerializedName("IdProg")
    val idProg: Int = 0,

    @SerializedName("IdWorker")
    val idWorker: String = "",

    @SerializedName("IdEnterprise")
    val idEnterprise: String = "",

    @SerializedName("QuestionCourseList")
    val questionCourseList: List<QuestionData> = emptyList()
)
package com.webcontrol.android.data.model

import com.google.gson.annotations.SerializedName

data class RequestExam(
    @SerializedName("ExamId")
    val examId:Int?,
    @SerializedName("ExamType")
    val examType: String?
){}

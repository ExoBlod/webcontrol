package com.webcontrol.angloamerican.data.network.response

import com.google.gson.annotations.SerializedName

data class AnswerQuestionData(
    @SerializedName("AnswerId")
    val answerId: Int,
    @SerializedName("Answer")
    val answer: String,
    @SerializedName("AnswerOrder")
    val answerOrder: Int,
    @SerializedName("IsCorrect")
    val isCorrect: Boolean,
    @SerializedName("AnswerMarked")
    var answerMarked: Boolean
)

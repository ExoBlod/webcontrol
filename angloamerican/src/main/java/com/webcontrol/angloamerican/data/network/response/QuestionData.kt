package com.webcontrol.angloamerican.data.network.response

import com.google.gson.annotations.SerializedName

data class QuestionData(
    @SerializedName("ExamId")
    val examId: Int,
    @SerializedName("QuestionPagesize")
    val questionPagesize: Int,
    @SerializedName("QuestionId")
    val questionId: Int,
    @SerializedName("Question")
    val question: String,
    @SerializedName("Order")
    val order: Int,
    @SerializedName("QuestionType")
    val questionType: Int,
    @SerializedName("Status")
    val status: String,
    @SerializedName("answerQuestionCourses")
    val answerQuestionCourses: List<AnswerQuestionData>
)

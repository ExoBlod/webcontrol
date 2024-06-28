package com.webcontrol.android.data.db.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "Encuestas")
class Encuestas (
    @PrimaryKey(autoGenerate = true)
    @NonNull
    val IdDb:Int = 0,

    @SerializedName(value = "PadreId", alternate = ["padreId"])
    val PadreId:Int? = 0,

    @SerializedName(value = "ExamId", alternate = ["examId"])
     val ExamId:Int? = 0,

    @SerializedName(value = "ExamType", alternate = ["examType"])
     val ExamType: String? = "",

    @SerializedName(value = "ExamName", alternate = ["examName"])
     val ExamName: String? = "",

    @SerializedName(value = "ExamObservation", alternate = ["examObservation"])
     val ExamObservation: String? = "",

    @SerializedName(value = "QuestionId", alternate = ["questionId"])
     val QuestionId:Int? = 0,

    @SerializedName(value = "QuestionText", alternate = ["questionText"])
     val QuestionText: String? = "",

    @SerializedName(value = "QuestionOrder", alternate = ["questionOrder"])
     val QuestionOrder:Int? = 0,

    @SerializedName(value = "IsCommented", alternate = ["isCommented"])
     val IsCommented:Boolean? = false,

    @SerializedName(value = "IsOrderer", alternate = ["isOrderer"])
     val IsOrderer:Boolean? = false,

    @SerializedName(value = "AnswerId", alternate = ["answerId"])
     val AnswerId:Int? = 0,

    @SerializedName(value = "AnswerText", alternate = ["answerText"])
     val AnswerText: String? = "",

    @SerializedName(value = "AnswerOrder", alternate = ["answerOrder"])
     val AnswerOrder:Int? = 0,

    @SerializedName(value = "IsCorrectAnswer", alternate = ["isCorrectAnswer"])
     val IsCorrectAnswer:Boolean? = false,

    @SerializedName(value = "IsMarked", alternate = ["isMarked"])
     val IsMarked:Boolean? = false,

    @SerializedName(value = "ExamDate", alternate = ["examDate"])
    val ExamDate:String? = "",

    @SerializedName(value = "ExamTime", alternate = ["examTime"])
    val ExamTime:String? = "",

    @SerializedName(value = "WorkerId", alternate = ["workerId"])
    val WorkerId:String? = "",

    var Estado:Int? = 0

) : Serializable {}
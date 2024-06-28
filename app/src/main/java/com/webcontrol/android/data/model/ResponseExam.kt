package com.webcontrol.android.data.model

import com.webcontrol.android.data.db.entity.Encuestas
import com.webcontrol.android.util.SharedUtils

data class AnswerExam(
    val AnswerId:Int? = 0,
    val AnswerText: String? = null,
    val AnswerOrder:Int? = 0,
    val IsCorrectAnswer:Boolean? = false,
    var IsMarked:Boolean? = false
){}

data class QuestionExam(
    val QuestionId:Int? = 0,
    val QuestionText: String? = null,
    val QuestionOrder:Int? = 0,
    val IsCommented:Boolean? = false,
    val IsOrderer:Boolean? = false,
    val Answers : List<AnswerExam>? = null
){}

data class ResponseExam(
    val ExamId:Int = 0,
    val ExamType: String? = null,
    val ExamName: String? = null,
    val ExamObservation: String? = null,
    val Questions: List<QuestionExam>? = null,
    var ExamDate: String? = SharedUtils.wCDate,
    var ExamTime: String? = SharedUtils.time,
    var WorkerId: String? = "",
    var Estado : Int = 0
){
    fun toEncuestas(lastInserted: Int) : List<Encuestas>{
        val lista: MutableList<Encuestas> = mutableListOf()
        Questions?.forEach { it ->
            it.Answers?.forEach{ it2 ->
                lista.add(Encuestas(PadreId = lastInserted,ExamId = ExamId,ExamType = ExamType,ExamName = ExamName,ExamObservation = ExamObservation,QuestionId = it.QuestionId,QuestionText = it.QuestionText,QuestionOrder = it.QuestionOrder,IsCommented = it.IsCommented,IsOrderer = it.IsOrderer,AnswerId = it2.AnswerId,AnswerText = it2.AnswerText,AnswerOrder = it2.AnswerOrder,IsCorrectAnswer = it2.IsCorrectAnswer,IsMarked = it2.IsMarked,ExamDate = ExamDate,ExamTime  = ExamTime,Estado = Estado))
            }
        }
        return lista.toList()
    }
}

data class EncuestasList(
    val ExamId:Int? = 0,
    val ExamType:String? = "",
    val ExamName:String? = "",
    val ExamObservation:String? = "",
    val ExamState:Int? = 0
){}

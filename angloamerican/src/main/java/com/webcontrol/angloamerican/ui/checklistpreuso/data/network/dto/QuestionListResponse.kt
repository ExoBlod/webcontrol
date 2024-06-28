package com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.webcontrol.angloamerican.ui.checklistpreuso.data.TypeAnswer
import com.webcontrol.angloamerican.ui.checklistpreuso.data.TypeQuestionResponse
import com.webcontrol.angloamerican.ui.checklistpreuso.data.utils.TypeQuestionResponseAdapter

data class QuestionListResponse(
    @SerializedName(value = "CheckListType")
    var checklistType: String,

    @SerializedName(value = "IdCheck")
    var idCheck: Int,

    @SerializedName(value = "NameCheck")
    var nameCheck: String,

    @SerializedName(value = "ChecklistActivo")
    var checklistActivo: String,

    @SerializedName(value = "CheckIdHead")
    var checkIdHead: Int,

    @SerializedName(value = "NameGroup")
    var nameGroup: String,

    @SerializedName(value = "CheckIdGroup")
    var checkIdGroup: Int,

    @SerializedName(value = "Status")
    var status: String,

    @SerializedName(value = "CheckComment")
    var checkComment: String,

    @SerializedName(value = "Data")
    var questions: List<Question>
){
    fun isComplete(): Boolean {
        questions.forEach {
            if (it.answer == null || it.answer == TypeAnswer.NN)
                return false
        }
        return true
    }

    fun hasOnlyYesAnswers(): QuestionListResponse {
        questions.forEach {
            it.answer = TypeAnswer.NO
        }
        return this
    }

    fun reloadAnswers(): QuestionListResponse {
        questions.forEach {
            it.answer = when (it.answer) {
                TypeAnswer.SI -> TypeAnswer.SI
                TypeAnswer.NO -> TypeAnswer.NO
                TypeAnswer.NA -> TypeAnswer.NA
                else -> TypeAnswer.NN
            }
        }
        return this
    }

}

data class Question(
    @SerializedName("ID_TIPO")
    var idTipo: String,

    @SerializedName("ID_CHECK")
    val idCheck: Int,

    @SerializedName("ID_CHECKGROUP")
    val groupId: Int,

    @SerializedName("NOMBRECHECKGROUP")
    val groupName: String,

    @SerializedName("ID_CHECKDET")
    val idCheckDet: Int,

    @SerializedName("NOMBRE")
    val name: String,

    @SerializedName("DESCRIPCION")
    val description: String,

    @SerializedName("REQFOTO")
    val reqPhoto: String,

    @SerializedName("REQCODBARRA")
    val reqBarCode: String,

    @SerializedName("REQDOCUMENTO")
    val reqDocument: String,

    @SerializedName("ORDEN")
    val order: Int,

    @SerializedName("IDSYNC")
    val idSync: Int,

    @SerializedName("USRCREA")
    var usrCrea: String,

    @SerializedName("FECCREA")
    val fecCrea: String,

    @SerializedName("USRMOD")
    val usrMod: String,

    @SerializedName("FECMOD")
    val fecMod: String,

    @SerializedName("REQVIDEO")
    val reqVideo: String,

    @SerializedName("TIPO")
    @JsonAdapter(TypeQuestionResponseAdapter::class)
    var type: TypeQuestionResponse? = TypeQuestionResponse.responseWithNA,

    @SerializedName("VALOR")
    var answer: TypeAnswer? = null,

    @SerializedName("VALORMULT")
    val valorMult: String,

    @SerializedName("CRITICO")
    val isCritical: Boolean,

    @SerializedName("FOTO")
    var photo: String,

    @SerializedName("CATEGORIA")
    var category : Int,

){
    companion object{
        val GREEN_STATUS = 1
        val YELLOW_STATUS = 2
        val RED_STATUS = 3
    }
}


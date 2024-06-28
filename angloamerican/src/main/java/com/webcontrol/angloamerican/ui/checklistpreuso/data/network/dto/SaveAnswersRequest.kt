package com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto

import com.google.gson.annotations.SerializedName

data class SaveAnswersRequest(
    @SerializedName("CheckListType")
    var checklistType: String,

    @SerializedName("IdCheck")
    var idCheck: Int,

    @SerializedName("NameCheck")
    var nameCheck: String,

    @SerializedName("ChecklistActivo")
    var checklistActivo: String,

    @SerializedName("CheckIdHead")
    var checkIdHead: Int,

    @SerializedName("NameGroup")
    var nameGroup: String,

    @SerializedName("CheckIdGroup")
    var checkIdGroup: Int,

    @SerializedName("Status")
    var status: String,

    @SerializedName("CheckComment")
    var checkComment: String,

    @SerializedName("Data")
    var data: List<Question>
)

fun mapToSaveAnswersRequest(questionList: QuestionListResponse): SaveAnswersRequest {
    return SaveAnswersRequest(
        checklistType = questionList.checklistType,
        idCheck = questionList.idCheck,
        nameCheck = questionList.nameCheck,
        checklistActivo = questionList.checklistActivo,
        checkIdHead = questionList.checkIdHead,
        nameGroup = questionList.nameGroup,
        checkIdGroup = questionList.checkIdGroup.toInt(),
        status = questionList.status,
        checkComment = questionList.checkComment,
        data = questionList.questions.map { mapToQuestion(it) }
    )
}

fun mapToQuestion(question: Question): Question {
    return Question(
        idTipo = question.idTipo,
        idCheck = question.idCheck,
        groupId = question.groupId,
        groupName = question.groupName,
        idCheckDet = question.idCheckDet,
        name = question.name,
        description = question.description,
        reqPhoto = question.reqPhoto,
        reqBarCode = question.reqBarCode,
        reqDocument = question.reqDocument,
        order = question.order,
        idSync = question.idSync,
        usrCrea = question.usrCrea,
        fecCrea = question.fecCrea,
        usrMod = question.usrMod,
        fecMod = question.fecMod,
        reqVideo = question.reqVideo,
        type = question.type,
        answer = question.answer,
        valorMult = question.valorMult,
        isCritical = question.isCritical,
        photo = question.photo,
        category = question.category
    )
}



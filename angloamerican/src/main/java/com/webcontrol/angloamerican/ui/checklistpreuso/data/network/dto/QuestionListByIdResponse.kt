package com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto

import com.google.gson.annotations.SerializedName

data class QuestionListByIdResponse(
    @SerializedName("CheckListType")
    var checkListType: String,

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
    var data: List<CheckListDataPreUso>
)
{
    fun toMapper(): HistoryResponse{
        return HistoryResponse(
            checklistName = this.nameCheck,
            checkingInHead = this.checkIdHead,
            checklistStatus = this.status,
            checklistDate = "",
            checklistRight = 0,
            plate = "",
            validate = 0,
            workerId = "",
            workerName = ""
        )
    }
}
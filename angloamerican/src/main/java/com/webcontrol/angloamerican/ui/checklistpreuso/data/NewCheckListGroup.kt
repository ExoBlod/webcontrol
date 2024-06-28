package com.webcontrol.angloamerican.ui.checklistpreuso.data

data class NewCheckListGroup(
    val checkListType: String,
    val idCheck: Int,
    val nameCheck: String,
    val checklistActivo: String,
    var checkIdHead: Int,
    var nameGroup: String,
    var checkIdGroup: Int,
    var status: String = "E1",
    var data: List<NewCheckListQuestion>
) {
    fun isComplete(): Boolean {
        data.forEach {
            if (it.answer == null || it.answer == TypeAnswer.NN)
                return false
        }
        return true
    }

    fun reloadAnswers(): NewCheckListGroup {
        data.forEach {
            it.answer = when (it.valor.trim()) {
                "SI" -> TypeAnswer.SI
                "NO" -> TypeAnswer.NO
                "NA" -> TypeAnswer.NA
                else -> TypeAnswer.NN
            }
        }
        return this
    }
}
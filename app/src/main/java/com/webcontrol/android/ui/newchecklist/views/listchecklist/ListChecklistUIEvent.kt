package com.webcontrol.android.ui.newchecklist.views.listchecklist

import com.webcontrol.android.ui.newchecklist.data.NewCheckListGroup
import com.webcontrol.android.ui.newchecklist.data.NewCheckListQuestion

sealed class ListChecklistUIEvent{
    data class Success(val listChecklist: List<NewCheckListGroup>) : ListChecklistUIEvent()
    data class SuccessAnswerSaving(val listChecklist: List<NewCheckListQuestion>) : ListChecklistUIEvent()
    object Error : ListChecklistUIEvent()
    object ShowLoading : ListChecklistUIEvent()
    object HideLoading : ListChecklistUIEvent()
}

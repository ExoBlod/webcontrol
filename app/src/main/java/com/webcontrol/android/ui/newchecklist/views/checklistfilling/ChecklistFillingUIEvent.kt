package com.webcontrol.android.ui.newchecklist.views.checklistfilling

import com.webcontrol.android.ui.newchecklist.data.NewCheckListGroup

sealed class ChecklistFillingUIEvent{
    data class Success(val listFilling: List<NewCheckListGroup>) : ChecklistFillingUIEvent()
    object Error : ChecklistFillingUIEvent()
    object ShowLoading : ChecklistFillingUIEvent()
    object HideLoading : ChecklistFillingUIEvent()
}
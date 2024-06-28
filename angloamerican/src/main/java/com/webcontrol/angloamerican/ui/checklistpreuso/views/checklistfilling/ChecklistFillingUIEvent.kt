package com.webcontrol.angloamerican.ui.checklistpreuso.views.checklistfilling

import com.webcontrol.angloamerican.ui.checklistpreuso.data.NewCheckListGroup

sealed class ChecklistFillingUIEvent{
    data class Success(val listFilling: List<NewCheckListGroup>) : ChecklistFillingUIEvent()
    object Error : ChecklistFillingUIEvent()
    object ShowLoading : ChecklistFillingUIEvent()
    object HideLoading : ChecklistFillingUIEvent()
}
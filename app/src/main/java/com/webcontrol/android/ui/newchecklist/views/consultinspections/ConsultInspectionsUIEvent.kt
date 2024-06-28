package com.webcontrol.android.ui.newchecklist.views.consultinspections

import com.webcontrol.android.ui.newchecklist.data.NewCheckListHistory

sealed class ConsultInspectionsUIEvent{
    data class Success(val listHistory: List<NewCheckListHistory>) : ConsultInspectionsUIEvent()
    object Error : ConsultInspectionsUIEvent()
    object ShowLoading : ConsultInspectionsUIEvent()
    object HideLoading : ConsultInspectionsUIEvent()
}

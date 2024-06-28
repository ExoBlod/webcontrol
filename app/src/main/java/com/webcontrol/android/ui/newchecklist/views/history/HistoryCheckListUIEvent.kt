package com.webcontrol.android.ui.newchecklist.views.history

import com.webcontrol.android.ui.newchecklist.data.NewCheckListGroup
import com.webcontrol.android.ui.newchecklist.data.NewCheckListHistory
import com.webcontrol.android.ui.newchecklist.data.NewCheckListQuestion

sealed class HistoryCheckListUIEvent{
    data class Success(val listHistory: List<NewCheckListHistory>) : HistoryCheckListUIEvent()
    data class SuccessSearchByCheckingHead(val listHistory: List<NewCheckListGroup>) : HistoryCheckListUIEvent()
    object Error : HistoryCheckListUIEvent()
    object ShowLoading : HistoryCheckListUIEvent()
    object HideLoading : HistoryCheckListUIEvent()
}

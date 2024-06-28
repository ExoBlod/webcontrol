package com.webcontrol.android.ui.newchecklist.views.history

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.android.bambas.repositories.NewCheckListRepository
import com.webcontrol.android.ui.newchecklist.data.WorkerSignature
import com.webcontrol.android.ui.newchecklist.views.listchecklist.ListChecklistUIEvent
import com.webcontrol.android.util.SharedUtils
import com.webcontrol.android.util.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryChecklistViewModel @Inject
constructor(
    @ApplicationContext val context: Context,
    private val newCheckListRepository: NewCheckListRepository
) : ViewModel(){
    private val _listHistory = MutableSharedFlow<HistoryCheckListUIEvent>()
    val listHistory get() = _listHistory.asSharedFlow()

    fun getHistory(workerId: String) = launch {
        _listHistory.emit(HistoryCheckListUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                newCheckListRepository.getWorkerHistory(workerId)
            }.onSuccess { listNewChecklist ->
                _listHistory.emit(HistoryCheckListUIEvent.Success(listNewChecklist))
            }.onFailure {
                _listHistory.emit(HistoryCheckListUIEvent.Error)
            }
        }
        _listHistory.emit(HistoryCheckListUIEvent.HideLoading)
    }

    fun searchbyChechingHead(idHead: Int) = launch {
        _listHistory.emit(HistoryCheckListUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                newCheckListRepository.getListQuestionByHead(idHead)
            }.onSuccess { listNewChecklist ->
                _listHistory.emit(HistoryCheckListUIEvent.SuccessSearchByCheckingHead(listNewChecklist))
            }.onFailure {
                _listHistory.emit(HistoryCheckListUIEvent.Error)
            }
        }
        _listHistory.emit(HistoryCheckListUIEvent.HideLoading)
    }
}
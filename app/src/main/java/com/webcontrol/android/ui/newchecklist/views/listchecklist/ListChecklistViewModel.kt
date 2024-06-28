package com.webcontrol.android.ui.newchecklist.views.listchecklist

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.android.bambas.repositories.NewCheckListRepository
import com.webcontrol.android.ui.newchecklist.data.NewCheckListGroup
import com.webcontrol.android.ui.newchecklist.views.history.HistoryCheckListUIEvent
import com.webcontrol.android.util.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListChecklistViewModel @Inject
constructor(
    @ApplicationContext val context: Context,
    private val newCheckListRepository: NewCheckListRepository
) : ViewModel() {
    private val _listCheckList = MutableSharedFlow<ListChecklistUIEvent>()
    val listCheckList get() = _listCheckList.asSharedFlow()

    fun getListCheckList() = launch {
        _listCheckList.emit(ListChecklistUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                newCheckListRepository.questionLists()
            }.onSuccess { listChecklist ->
                _listCheckList.emit(ListChecklistUIEvent.Success(listChecklist))
            }.onFailure {
                _listCheckList.emit(ListChecklistUIEvent.Error)
            }
        }
        _listCheckList.emit(ListChecklistUIEvent.HideLoading)
    }

    fun savingAnswer(listAnswer : List<NewCheckListGroup>) = launch {
        _listCheckList.emit(ListChecklistUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                newCheckListRepository.savingAnswer(listAnswer)
            }.onSuccess { listQuestion ->
                _listCheckList.emit(ListChecklistUIEvent.SuccessAnswerSaving(listQuestion))
            }.onFailure {
                _listCheckList.emit(ListChecklistUIEvent.Error)
            }
        }
        _listCheckList.emit(ListChecklistUIEvent.HideLoading)
    }
/*
    fun searchbyChechingHead(idHead: Int) = launch {
        _listHistory.emit(HistoryCheckListUIEvent.SuccessSearchByCheckingHead(listOf()))
        viewModelScope.launch {
            runCatching {
                newCheckListRepository.getListQuestionByHead(idHead)
            }.onSuccess { listNewChecklist ->
                _listHistory.emit(HistoryCheckListUIEvent.SuccessSearchList(listNewChecklist))
            }.onFailure {
                _listHistory.emit(HistoryCheckListUIEvent.Error)
            }
        }
        _listHistory.emit(HistoryCheckListUIEvent.HideLoading)
*/
    fun noHacer()= launch {
        _listCheckList.emit(ListChecklistUIEvent.SuccessAnswerSaving(listOf()))
    }
}
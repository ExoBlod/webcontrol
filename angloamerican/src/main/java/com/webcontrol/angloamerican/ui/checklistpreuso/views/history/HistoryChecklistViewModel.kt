package com.webcontrol.angloamerican.ui.checklistpreuso.views.history

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.webcontrol.angloamerican.ui.checklistpreuso.data.network.dto.QuestionListByIdRequest
import com.webcontrol.angloamerican.ui.checklistpreuso.usecases.GetHistoryPreUsoUseCase
import com.webcontrol.angloamerican.ui.checklistpreuso.usecases.GetQuestionListUseCase
import com.webcontrol.angloamerican.ui.checklistpreuso.usecases.GetQuestionsByIdHeadUseCase
import com.webcontrol.angloamerican.utils.launch
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
    val getHistoryPreUsoUseCase: GetHistoryPreUsoUseCase,
    val getQuestionsByIdHeadUseCase: GetQuestionsByIdHeadUseCase
) : ViewModel(){
    private val _listHistory = MutableSharedFlow<HistoryCheckListUIEvent>()
    val listHistory get() = _listHistory.asSharedFlow()

    fun getHistory(workerId: String) = launch {
        _listHistory.emit(HistoryCheckListUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                getHistoryPreUsoUseCase(workerId)
            }.onSuccess {
                _listHistory.emit(HistoryCheckListUIEvent.Success(it))
                _listHistory.emit(HistoryCheckListUIEvent.HideLoading)
            }.onFailure {
                val message = it.message.toString()
                _listHistory.emit(HistoryCheckListUIEvent.ErrorString(message))
                _listHistory.emit(HistoryCheckListUIEvent.HideLoading)
            }
        }
    }

    fun searchbyChechingHead(idHead: Int) = launch {
        _listHistory.emit(HistoryCheckListUIEvent.ShowLoading)
        viewModelScope.launch {
            runCatching {
                getQuestionsByIdHeadUseCase(idHead)
            }.onSuccess { listNewChecklist ->
                _listHistory.emit(HistoryCheckListUIEvent.SuccessSearchByCheckingHeadId(listNewChecklist))
            }.onFailure {
                _listHistory.emit(HistoryCheckListUIEvent.Error)
            }
        }
        _listHistory.emit(HistoryCheckListUIEvent.HideLoading)
    }
}